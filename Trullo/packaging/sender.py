"""Copia solo el perfil emisor y SMTP. Nunca copia la configuración de base de desarrollo."""
import json
from pathlib import Path
import shutil

CACHE_NAMES = {"Cache", "Code Cache", "GPUCache", "ShaderCache", "GrShaderCache",
               "DawnGraphiteCache", "DawnWebGPUCache", "Crashpad", "BrowserMetrics",
               "SingletonLock", "SingletonSocket", "SingletonCookie", "DevToolsActivePort", "LOCK"}


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


def copy_sender(session, env_file, destination):
    session = Path(session)
    if not (session / "Default").is_dir():
        raise RuntimeError("Falta el perfil Default de la sesion emisora")
    if (session / "SingletonLock").exists() or (session / "SingletonSocket").exists():
        raise RuntimeError("Cierra WhatsApp/Chromium antes de copiar la sesion")
    def ignore(directory, names):
        return [name for name in names if name in CACHE_NAMES or (Path(directory) / name).is_symlink()]
    shutil.copytree(session, destination / "session", ignore=ignore)
    mail = {key: value for key, value in read_env(env_file).items() if key in {"SMTP_HOST", "SMTP_PORT", "SMTP_USER", "SMTP_PASSWORD", "SMTP_FROM"}}
    if not all(mail.get(key) for key in ["SMTP_HOST", "SMTP_USER", "SMTP_PASSWORD"]):
        raise RuntimeError("El administrador debe preparar SMTP_HOST, SMTP_USER y SMTP_PASSWORD antes de distribuir")
    (destination / "mail.json").write_text(json.dumps(mail), encoding="utf-8")
