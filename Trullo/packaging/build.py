#!/usr/bin/env python3
"""Construye en el SO destino. No instala ni inicia PostgreSQL/WhatsApp."""
import argparse
import hashlib
import os
from pathlib import Path
import platform
import shutil
import subprocess
import sys
import tarfile
from sender import copy_sender, read_env

ROOT = Path(__file__).resolve().parents[1]
VERSION = "1.0.0"


def run(*command):
    subprocess.run([str(item) for item in command], cwd=ROOT, check=True)


def install_linux_launcher(bundle):
    # Mantener el launcher Java junto a su configuración de jpackage, pero interno.
    (bundle / "bin/Trullo").rename(bundle / "bin/Trullo-java")
    (bundle / "lib/app/Trullo.cfg").rename(bundle / "lib/app/Trullo-java.cfg")
    for name in ["Trullo", "trullo"]:
        destination = bundle / "bin" / name
        shutil.copy2(ROOT / "packaging/linux/trullo", destination)
        destination.chmod(0o755)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--postgres-dir", type=Path, help="Windows: ZIP oficial extraido, raiz con bin/lib/share")
    parser.add_argument("--vc-redist", type=Path, help="Windows: vc_redist.x64.exe oficial")
    parser.add_argument("--iscc", default="ISCC.exe")
    parser.add_argument("--sender-session", type=Path, help="Sesion emisora detenida; por defecto WHATSPLAY_DIR/whatsapp_session")
    parser.add_argument("--sender-env", type=Path, default=ROOT / ".env", help="Configuracion SMTP del administrador")
    args = parser.parse_args()
    settings = read_env(args.sender_env)
    sender_session = args.sender_session
    if sender_session is None and settings.get("WHATSPLAY_DIR"):
        sender_session = Path(settings["WHATSPLAY_DIR"]) / "whatsapp_session"
    if sender_session is None or not sender_session.is_dir():
        raise SystemExit("Falta --sender-session: el usuario final no debe tener que vincular WhatsApp.")
    if not all(settings.get(key) for key in ["SMTP_HOST", "SMTP_USER", "SMTP_PASSWORD"]):
        raise SystemExit("Falta la configuracion SMTP del administrador en --sender-env.")
    windows = sys.platform == "win32"
    if platform.machine().lower() not in ("x86_64", "amd64") or sys.platform not in ("linux", "win32"):
        raise SystemExit("Por ahora se empaqueta Windows y Linux x64.")
    if windows and (not args.postgres_dir or not args.vc_redist):
        raise SystemExit("Windows requiere --postgres-dir y --vc-redist. No se descargan ejecutables implicitamente.")
    if windows:
        if not (args.postgres_dir / "bin/initdb.exe").is_file() or not (args.postgres_dir / "share/postgres.bki").is_file():
            raise SystemExit("El directorio PostgreSQL no contiene los binarios y share oficiales.")
        if not args.vc_redist.is_file():
            raise SystemExit("No existe vc_redist.x64.exe")
    uv = shutil.which("uv")
    if not uv:
        raise SystemExit("Falta uv en el equipo de compilacion.")
    run("mvn.cmd" if windows else "mvn", "-B", "-ntp", "-Pinstaller", "clean", "package")
    work = ROOT / "target/installer"
    work.mkdir(mode=0o700)
    input_dir = work / "input"
    input_dir.mkdir()
    shutil.copy2(ROOT / "target/Task-1.0-SNAPSHOT.jar", input_dir)
    runtime = work / "runtime"
    # Proveedores explícitos: --bind-services puede incorporar jdk.jlink y exigir
    # jmods ausentes en algunos JDK distribuidos como imágenes de runtime.
    run("jlink", "--add-modules", "java.se,jdk.crypto.ec,jdk.unsupported,jdk.charsets,jdk.naming.dns,jdk.zipfs",
        "--strip-debug", "--no-header-files", "--no-man-pages", "--output", runtime)
    run("jpackage", "--type", "app-image", "--name", "Trullo", "--app-version", VERSION,
        "--input", input_dir, "--main-jar", "Task-1.0-SNAPSHOT.jar", "--main-class", "com.trullo.TrulloApp",
        "--runtime-image", runtime, "--java-options", "--enable-native-access=ALL-UNNAMED", "--dest", work / "image")
    bundle = work / "image/Trullo"
    probe = work / "probe"
    probe.mkdir()
    jar = input_dir / "Task-1.0-SNAPSHOT.jar"
    run("javac", "-encoding", "UTF-8", "-cp", jar, "-d", probe, ROOT / "packaging/tests/RuntimeProbe.java")
    (probe / ".env").write_text('DB_NAME=runtime-probe\nWHATSPLAY_DIR="' + probe.as_posix() + '"\n', encoding="utf-8")
    probe_env = os.environ.copy()
    probe_env.update(TRULLO_CONFIG_DIR=str(probe), TRULLO_HOME=str(bundle))
    java = bundle / ("runtime/bin/java.exe" if windows else "lib/runtime/bin/java")
    subprocess.run([str(java), "-Djava.awt.headless=true", "-cp", str(jar) + os.pathsep + str(probe),
                    "RuntimeProbe"], cwd=probe, env=probe_env, check=True)
    setup = bundle / "setup"
    setup.mkdir()
    shutil.copy2(ROOT / "packaging/README-user.md", bundle / "README.md")
    shutil.copy2(ROOT / "packaging/bootstrap.py", setup)
    shutil.copy2(ROOT / "scripts/send_whatsapp.py", setup)
    copy_sender(sender_session, args.sender_env, setup / "sender")
    shutil.copytree(ROOT / "packaging/runtime", setup / "runtime")
    shutil.copytree(ROOT / "src/main/resources/sql", setup / "sql", ignore=shutil.ignore_patterns("seed.sql"))
    tools = bundle / "tools"
    tools.mkdir()
    shutil.copy2(uv, tools / ("uv.exe" if windows else "uv"))
    dist = ROOT / "target/dist"
    dist.mkdir(mode=0o700)
    if windows:
        for name in ["bin", "lib", "share"]:
            shutil.copytree(args.postgres_dir / name, bundle / "postgres" / name)
        shutil.copy2(args.vc_redist, tools / "vc_redist.x64.exe")
        shutil.copy2(ROOT / "packaging/windows/launch.ps1", setup)
        run(args.iscc, "/DPayloadRoot=" + str(bundle), "/DAppVersion=" + VERSION,
            "/O" + str(dist), ROOT / "packaging/windows/Trullo.iss")
    else:
        shutil.copy2(ROOT / "packaging/linux/launch.sh", setup)
        shutil.copy2(ROOT / "packaging/linux/install.sh", bundle / "install.sh")
        (setup / "launch.sh").chmod(0o755)
        (bundle / "install.sh").chmod(0o755)
        install_linux_launcher(bundle)
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
