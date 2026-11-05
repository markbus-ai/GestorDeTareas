"""Descarga privada para CI. URL en TRULLO_SENDER_URL; nunca se imprime."""
import os
from pathlib import Path
import sys
import tempfile
import urllib.request
import zipfile

url = os.environ.get('TRULLO_SENDER_URL', '')
if not url.startswith('https://'):
    raise SystemExit('Configura el secreto HTTPS del perfil emisor para este sistema.')
destination = Path(sys.argv[1]).resolve()
destination.mkdir(parents=True, exist_ok=True)
try:
    with tempfile.TemporaryFile() as output:
        with urllib.request.urlopen(url, timeout=60) as response:
            while chunk := response.read(1024 * 1024):
                output.write(chunk)
        output.seek(0)
        with zipfile.ZipFile(output) as archive:
            for entry in archive.infolist():
                path = (destination / entry.filename).resolve()
                if not path.is_relative_to(destination) or '\\' in entry.filename:
                    raise ValueError('Ruta no permitida en perfil emisor')
            archive.extractall(destination)
    if not (destination / 'session/Default').is_dir() or not (destination / 'mail.env').is_file():
        raise ValueError('El perfil exportado no contiene session/Default y mail.env')
except Exception:
    raise SystemExit('No se pudo descargar o verificar el perfil emisor privado.') from None
