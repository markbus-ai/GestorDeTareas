#!/usr/bin/env python3
"""Construye el instalador en el SO destino. No instala ni inicia PostgreSQL."""
import argparse
import hashlib
import os
from pathlib import Path
import platform
import shutil
import subprocess
import sys
import tarfile

ROOT = Path(__file__).resolve().parents[1]
VERSION = "1.0.0"
JAR_NAME = "Task-1.0-SNAPSHOT.jar"
PRIVATE_KEYS = [
    "WHATSAPP_API_URL", "WHATSAPP_API_ZONE", "API_KEY",
    "SMTP_HOST", "SMTP_PORT", "SMTP_USER", "SMTP_PASSWORD", "SMTP_FROM",
]


def run(*command):
    subprocess.run([str(item) for item in command], cwd=ROOT, check=True)


def read_env(path):
    values = {}
    if path and Path(path).is_file():
        for line in Path(path).read_text(encoding="utf-8").splitlines():
            if "=" in line and not line.lstrip().startswith("#"):
                key, value = line.split("=", 1)
                value = value.strip()
                if len(value) >= 2 and value[0] == value[-1] and value[0] in "\"'":
                    value = value[1:-1]
                values[key.strip()] = value
    return values


def private_values(settings):
    values = {}
    for key in PRIVATE_KEYS:
        value = settings.get(key, "").strip()
        if not value:
            continue
        if any(character in value for character in ['"', "\n", "\r"]):
            raise SystemExit("La configuracion privada tiene un valor invalido en " + key)
        values[key] = value
    if not values.get("WHATSAPP_API_URL") or not values.get("API_KEY"):
        raise SystemExit("Faltan WHATSAPP_API_URL y API_KEY en --config-env.")
    values.setdefault("WHATSAPP_API_ZONE", "America/Argentina/Buenos_Aires")
    return values


def private_env_text(values):
    return "\n".join(key + '="' + values[key] + '"' for key in PRIVATE_KEYS if key in values) + "\n"


def require_tools(names):
    missing = [name for name in names if not shutil.which(name)]
    if missing:
        raise SystemExit("Faltan herramientas en PATH: " + ", ".join(missing))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--config-env", type=Path, default=ROOT / ".env",
                        help="Configuracion privada de API y SMTP del administrador")
    parser.add_argument("--postgres-dir", type=Path,
                        help="Windows: ZIP oficial extraido, raiz con bin/lib/share")
    parser.add_argument("--vc-redist", type=Path, help="Windows: vc_redist.x64.exe oficial")
    parser.add_argument("--iscc", default="ISCC.exe")
    args = parser.parse_args()

    values = private_values(read_env(args.config_env))
    windows = sys.platform == "win32"
    if platform.machine().lower() not in ("x86_64", "amd64") or sys.platform not in ("linux", "win32"):
        raise SystemExit("Por ahora se empaqueta Windows y Linux x64.")
    if windows:
        if not args.postgres_dir or not args.vc_redist:
            raise SystemExit("Windows requiere --postgres-dir y --vc-redist.")
        if not (args.postgres_dir / "bin/initdb.exe").is_file() \
                or not (args.postgres_dir / "share/postgres.bki").is_file():
            raise SystemExit("El directorio PostgreSQL no contiene binarios y share oficiales.")
        if not args.vc_redist.is_file():
            raise SystemExit("No existe vc_redist.x64.exe")
        require_tools(["mvn.cmd", "javac", "jlink", "jpackage"])
    else:
        require_tools(["mvn", "javac", "jlink", "jpackage"])

    run("mvn.cmd" if windows else "mvn", "-B", "-ntp", "-Pinstaller", "clean", "package")
    work = ROOT / "target/installer"
    work.mkdir(mode=0o700)
    input_dir = work / "input"
    input_dir.mkdir()
    shutil.copy2(ROOT / "target" / JAR_NAME, input_dir)

    runtime = work / "runtime"
    # Proveedores explicitos: --bind-services puede incluir jdk.jlink y exigir
    # jmods ausentes en algunos JDK distribuidos como imagenes de runtime.
    run("jlink", "--add-modules",
        "java.se,jdk.crypto.ec,jdk.localedata,jdk.unsupported,jdk.charsets,jdk.naming.dns,jdk.zipfs",
        "--strip-debug", "--no-header-files", "--no-man-pages", "--output", runtime)
    run("jpackage", "--type", "app-image", "--name", "Trullo", "--app-version", VERSION,
        "--input", input_dir, "--main-jar", JAR_NAME, "--main-class", "com.trullo.setup.SetupMain",
        "--runtime-image", runtime, "--java-options", "--enable-native-access=ALL-UNNAMED",
        "--dest", work / "image")
    bundle = work / "image/Trullo"

    probe = work / "probe"
    probe.mkdir()
    jar = input_dir / JAR_NAME
    run("javac", "-encoding", "UTF-8", "-cp", jar, "-d", probe, ROOT / "packaging/tests/RuntimeProbe.java")
    (probe / ".env").write_text(
        "\n".join([
            "DB_NAME=runtime-probe",
            'WHATSAPP_API_URL="http://127.0.0.1/trullo"',
            'API_KEY="runtime-probe"',
            'WHATSAPP_API_ZONE="UTC"',
        ]) + "\n",
        encoding="utf-8",
    )
    probe_env = os.environ.copy()
    probe_env.update(TRULLO_CONFIG_DIR=str(probe), TRULLO_HOME=str(bundle))
    java = bundle / ("runtime/bin/java.exe" if windows else "lib/runtime/bin/java")
    subprocess.run([str(java), "-Djava.awt.headless=true", "-cp", str(jar) + os.pathsep + str(probe),
                    "RuntimeProbe"], cwd=probe, env=probe_env, check=True)

    setup = bundle / "setup"
    setup.mkdir()
    shutil.copy2(ROOT / "packaging/README.md", bundle / "README.md")
    shutil.copytree(ROOT / "src/main/resources/sql", setup / "sql",
                    ignore=shutil.ignore_patterns("seed.sql"))
    private_env = setup / "private.env"
    private_env.write_text(private_env_text(values), encoding="utf-8")
    if not windows:
        private_env.chmod(0o600)

    dist = ROOT / "target/dist"
    dist.mkdir(mode=0o700)
    if windows:
        for name in ["bin", "lib", "share"]:
            shutil.copytree(args.postgres_dir / name, bundle / "postgres" / name)
        tools = bundle / "tools"
        tools.mkdir()
        shutil.copy2(args.vc_redist, tools / "vc_redist.x64.exe")
        run(args.iscc, "/DPayloadRoot=" + str(bundle), "/DAppVersion=" + VERSION,
            "/O" + str(dist), ROOT / "packaging/windows/Trullo.iss")
    else:
        install = bundle / "install.sh"
        shutil.copy2(ROOT / "packaging/linux/install.sh", install)
        install.chmod(0o755)
        with tarfile.open(dist / f"Trullo-{VERSION}-linux-x64.tar.gz", "w:gz") as archive:
            archive.add(bundle, arcname="Trullo")

    checksums = []
    for artifact in sorted(dist.iterdir()):
        if artifact.name.endswith((".exe", ".tar.gz")):
            with artifact.open("rb") as stream:
                checksums.append(hashlib.file_digest(stream, "sha256").hexdigest() + "  " + artifact.name)
    (dist / "SHA256SUMS").write_text("\n".join(checksums) + "\n", encoding="ascii")
    print("Artefactos preparados en", dist)


if __name__ == "__main__":
    main()
