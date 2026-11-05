#!/usr/bin/env python3
"""Exporta un perfil emisor detenido y solo SMTP para un build privado."""
import argparse
from pathlib import Path
import shutil
import tempfile
from sender import copy_sender, read_env

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--env', type=Path, default=root / '.env')
parser.add_argument('--session', type=Path)
parser.add_argument('--output', type=Path, default=root / 'target/sender-private.zip')
args = parser.parse_args()
values = read_env(args.env)
session = args.session or Path(values.get('WHATSPLAY_DIR', '')) / 'whatsapp_session'
args.output.parent.mkdir(parents=True, exist_ok=True)
with tempfile.TemporaryDirectory(prefix='trullo-sender-export-') as directory:
    destination = Path(directory)
    copy_sender(session, args.env, destination)
    mail = __import__('json').loads((destination / 'mail.json').read_text())
    (destination / 'mail.json').unlink()
    (destination / 'mail.env').write_text('\n'.join(k + '="' + v + '"' for k, v in mail.items()) + '\n')
    shutil.make_archive(str(args.output.with_suffix('')), 'zip', destination)
print('Exportacion privada preparada. Contiene acceso a WhatsApp y SMTP: no subir al repositorio.')
