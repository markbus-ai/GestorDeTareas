#!/usr/bin/env python3
"""Preparación y arranque por usuario. Nunca usa ni borra una base PostgreSQL externa."""
import argparse
from contextlib import contextmanager
import hashlib
import json
import os
from pathlib import Path
import secrets
import shutil
import subprocess
import sys
import tempfile


def user_paths():
    home = Path.home()
    if sys.platform == "win32":
        return (Path(os.environ.get("APPDATA", home / "AppData/Roaming")) / "Trullo",
                Path(os.environ.get("LOCALAPPDATA", home / "AppData/Local")) / "Trullo")
    def xdg(name, fallback):
        value = os.environ.get(name, "")
        return Path(value) if value and Path(value).is_absolute() else fallback
    return xdg("XDG_CONFIG_HOME", home / ".config") / "trullo", xdg("XDG_DATA_HOME", home / ".local/share") / "trullo"


def private_write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    fd, temporary = tempfile.mkstemp(prefix=".trullo-", dir=path.parent)
    try:
        with os.fdopen(fd, "w", encoding="utf-8", newline="\n") as output:
            output.write(text)
            output.flush()
            os.fsync(output.fileno())
        os.replace(temporary, path)
    finally:
        if os.path.exists(temporary):
            os.unlink(temporary)


@contextmanager
def setup_lock(directory):
    directory.mkdir(parents=True, exist_ok=True, mode=0o700)
    with (directory / "setup.lock").open("a+b") as stream:
        if sys.platform == "win32":
            import msvcrt
            if stream.tell() == 0:
                stream.write(b"\0")
                stream.flush()
            stream.seek(0)
            msvcrt.locking(stream.fileno(), msvcrt.LK_NBLCK, 1)
        else:
            import fcntl
            try:
                fcntl.flock(stream, fcntl.LOCK_EX | fcntl.LOCK_NB)
            except BlockingIOError as error:
                raise RuntimeError("Trullo ya esta abierto. Cerralo antes de preparar o vincular WhatsApp.") from error
        try:
            yield
        finally:
            if sys.platform == "win32":
                stream.seek(0)
                msvcrt.locking(stream.fileno(), msvcrt.LK_UNLCK, 1)
            else:
                fcntl.flock(stream, fcntl.LOCK_UN)


def pg_bin(installation, override=None):
    if override:
        directory = Path(override)
    elif sys.platform == "win32":
        directory = installation / "postgres/bin"
    else:
        executable = shutil.which("pg_config")
        if executable:
            directory = Path(subprocess.check_output([executable, "--bindir"], text=True).strip())
        elif shutil.which("initdb"):
            directory = Path(shutil.which("initdb")).parent
        else:
            candidates = sorted(Path("/usr/lib/postgresql").glob("*/bin"), reverse=True)
            if not candidates:
                raise RuntimeError("Falta PostgreSQL. Ejecuta el instalador de Linux primero.")
            directory = candidates[0]
    suffix = ".exe" if sys.platform == "win32" else ""
    result = {name: directory / (name + suffix) for name in ["initdb", "pg_ctl", "psql"]}
    if not all(path.is_file() for path in result.values()):
        raise RuntimeError("No se encuentran todos los binarios de PostgreSQL.")
    return result


def database_state(directory, port):
    path = directory / "database-state.json"
    if path.exists():
        state = json.loads(path.read_text(encoding="utf-8"))
        if state.get("format") != 1:
            raise RuntimeError("Formato de configuracion de base no compatible.")
        return state
    if (directory / "postgres/PG_VERSION").exists():
        raise RuntimeError("Existe una base sin su configuracion. No se modifico; restaura database-state.json.")
    state = {"format": 1, "port": port, "admin_password": secrets.token_hex(32),
             "app_password": secrets.token_hex(32)}
    private_write(path, json.dumps(state))
    return state


def prepare_database(installation, data, binaries, port=54329):
    state = database_state(data, port)
    cluster = data / "postgres"
    if not (cluster / "PG_VERSION").exists():
        # initdb rechaza directorios no vacíos; nunca borrar ni reinicializar uno existente.
        fd, password_file = tempfile.mkstemp(prefix=".pg-pass-", dir=data)
        try:
            with os.fdopen(fd, "w", encoding="ascii") as output:
                output.write(state["admin_password"] + "\n")
            subprocess.run([str(binaries["initdb"]), "-D", str(cluster), "-U", "trullo_admin",
                            "--auth=scram-sha-256", "--encoding=UTF8", "--no-locale",
                            "--pwfile=" + password_file], check=True, stdout=subprocess.DEVNULL)
        finally:
            os.unlink(password_file)
        # TCP solo local. Puerto privado, independiente del 5432 de otras instalaciones.
        with (cluster / "postgresql.conf").open("a", encoding="utf-8") as output:
            socket = "" if sys.platform == "win32" else str(cluster).replace("'", "''")
            output.write(f"\nlisten_addresses = '127.0.0.1'\nport = {int(state['port'])}\n"
                         f"unix_socket_directories = '{socket}'\n")
    status = subprocess.run([str(binaries["pg_ctl"]), "-D", str(cluster), "status"],
                            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    if status.returncode != 0:
        subprocess.run([str(binaries["pg_ctl"]), "-D", str(cluster), "-l", str(data / "postgres.log"),
                        "-w", "-t", "30", "start"], check=True)
    env = os.environ.copy()
    env.update(PGHOST="127.0.0.1", PGPORT=str(state["port"]), PGUSER="trullo_admin",
               PGPASSWORD=state["admin_password"], PGCONNECT_TIMEOUT="5", PGCLIENTENCODING="UTF8")
    def sql(query, database="postgres"):
        result = subprocess.run([str(binaries["psql"]), "-X", "-v", "ON_ERROR_STOP=1", "-At", "-d", database],
                                input=query, text=True, encoding="utf-8", env=env, capture_output=True)
        if result.returncode:
            raise RuntimeError("No se pudo preparar la base privada de Trullo: " + result.stderr.strip())
        return result.stdout.strip()
    if sql("SELECT 1 FROM pg_roles WHERE rolname='trullo_app';") != "1":
        sql("CREATE ROLE trullo_app LOGIN PASSWORD '" + state["app_password"] + "';")
    if sql("SELECT 1 FROM pg_database WHERE datname='trullo';") != "1":
        sql("CREATE DATABASE trullo OWNER trullo_app;")
    marker = sql("SELECT to_regclass('public.trullo_schema_version');", "trullo")
    if not marker:
        if sql("SELECT count(*) FROM pg_tables WHERE schemaname='public';", "trullo") != "0":
            raise RuntimeError("La base tiene tablas ajenas al instalador. No se modifico ninguna tabla.")
        enums = (installation / "setup/sql/enums.sql").read_text(encoding="utf-8")
        schema = (installation / "setup/sql/schema.sql").read_text(encoding="utf-8")
        schema = "\n".join(line for line in schema.splitlines() if not line.strip().lower().startswith("create database"))
        sql("BEGIN; SET ROLE trullo_app;\n" + enums + "\n" + schema
            + "\nCREATE TABLE trullo_schema_version(version integer PRIMARY KEY);"
              "INSERT INTO trullo_schema_version VALUES (1); COMMIT;", "trullo")
    elif sql("SELECT version FROM trullo_schema_version;", "trullo") != "1":
        raise RuntimeError("Version del schema no compatible. No se modifico la base.")
    return state


def prepare_whatsapp(installation, data, repair=False):
    source = installation / "setup/runtime"
    runtime = data / "whatsplay"
    runtime.mkdir(parents=True, exist_ok=True, mode=0o700)
    digest = hashlib.sha256()
    for name in ["pyproject.toml", "uv.lock", ".python-version"]:
        content = (source / name).read_bytes()
        digest.update(content)
        (runtime / name).write_bytes(content)
    marker = runtime / ".ready"
    fingerprint = digest.hexdigest()
    if repair or not marker.exists() or marker.read_text() != fingerprint or not (runtime / ".venv").exists():
        uv = installation / ("tools/uv.exe" if sys.platform == "win32" else "tools/uv")
        subprocess.run([str(uv), "sync", "--locked", "--project", str(runtime)], check=True)
        subprocess.run([str(uv), "run", "--project", str(runtime), "python", "-m", "playwright", "install", "chromium"], check=True)
        private_write(marker, fingerprint)
    return runtime


def prepare_sender(installation, data):
    source = installation / "setup/sender/session"
    destination = data / "whatsplay/whatsapp_session"
    if not source.is_dir():
        raise RuntimeError("El paquete no incluye la sesion emisora preparada por el administrador.")
    # Nunca reemplazar una sesión que el administrador ya haya preparado en ese equipo.
    if not destination.exists():
        shutil.copytree(source, destination)


def write_config(config, installation, data, state):
    path = config / ".env"
    if path.exists():
        # Actualizar instalación/ruta de runtime sin reemplazar las credenciales del usuario.
        lines = [line for line in path.read_text(encoding="utf-8").splitlines()
                 if not line.startswith(("UV_EXECUTABLE=", "WHATSPLAY_DIR="))]
    else:
        lines = ["DB_HOST=127.0.0.1", "DB_PORT=" + str(state["port"]), "DB_NAME=trullo",
                 "DB_USER=trullo_app", 'DB_PASSWORD="' + state["app_password"] + '"',
                 "# Email opcional: completar y reiniciar Trullo.", "SMTP_HOST=", "SMTP_USER=", "SMTP_PASSWORD="]
        sender_file = installation / "setup/sender/mail.json"
        if sender_file.exists():
            sender = json.loads(sender_file.read_text(encoding="utf-8"))
            allowed = {"SMTP_HOST", "SMTP_PORT", "SMTP_USER", "SMTP_PASSWORD", "SMTP_FROM"}
            lines = [line for line in lines if not line.startswith(("SMTP_HOST=", "SMTP_USER=", "SMTP_PASSWORD="))]
            for key, value in sender.items():
                if key not in allowed or not isinstance(value, str) or any(c in value for c in ['"', '\n', '\r']):
                    raise RuntimeError("Configuracion de correo del paquete invalida")
                lines.append(key + '="' + value + '"')
    uv = installation / ("tools/uv.exe" if sys.platform == "win32" else "tools/uv")
    lines.extend(['UV_EXECUTABLE="' + uv.as_posix() + '"',
                  'WHATSPLAY_DIR="' + (data / "whatsplay").as_posix() + '"'])
    private_write(path, "\n".join(lines) + "\n")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--home", type=Path, required=True)
    parser.add_argument("--setup-only", action="store_true")
    parser.add_argument("--repair", action="store_true")
    parser.add_argument("--config-dir", type=Path)
    parser.add_argument("--data-dir", type=Path)
    parser.add_argument("--pg-bin", type=Path)
    args = parser.parse_args()
    installation = args.home.resolve()
    default_config, default_data = user_paths()
    config, data = args.config_dir or default_config, args.data_dir or default_data
    data.mkdir(parents=True, exist_ok=True, mode=0o700)
    os.environ.update(TRULLO_HOME=str(installation), TRULLO_CONFIG_DIR=str(config),
                      PLAYWRIGHT_BROWSERS_PATH=str(data / "browsers"), UV_PYTHON_INSTALL_DIR=str(data / "python"),
                      UV_CACHE_DIR=str(data / "uv-cache"))
    with setup_lock(data):
        binaries = pg_bin(installation, args.pg_bin)
        try:
            print("Preparando la base de datos local...", flush=True)
            state = prepare_database(installation, data, binaries)
            print("Preparando WhatsApp...", flush=True)
            prepare_whatsapp(installation, data, args.repair)
            prepare_sender(installation, data)
            write_config(config, installation, data, state)
            if not args.setup_only:
                executable = installation / ("Trullo.exe" if sys.platform == "win32" else "bin/Trullo-java")
                return subprocess.call([str(executable)], cwd=data)
            print("Trullo esta preparado. La configuracion existente se conserva.")
            return 0
        finally:
            # Esta base pertenece exclusivamente al launcher; no dejar procesos que bloqueen
            # las actualizaciones ni detener otros servicios PostgreSQL del equipo.
            if (data / "postgres/PG_VERSION").exists():
                subprocess.run([str(binaries["pg_ctl"]), "-D", str(data / "postgres"),
                                "-m", "fast", "-w", "stop"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as error:
        print("No se pudo preparar Trullo: " + str(error), file=sys.stderr)
        sys.exit(1)
