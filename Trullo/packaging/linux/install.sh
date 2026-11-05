#!/usr/bin/env bash
set -euo pipefail
if [[ "$EUID" -eq 0 ]]; then
  echo 'Ejecuta este instalador con tu usuario normal; pedira sudo si necesita paquetes.' >&2
  exit 1
fi
SOURCE="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
DESTINATION="${XDG_DATA_HOME:-$HOME/.local/share}/trullo/application"
[[ "$DESTINATION" = /* ]] || DESTINATION="$HOME/.local/share/trullo/application"
if [[ -e "$DESTINATION" ]]; then
  echo 'Ya existe una instalacion. Cierra Trullo y renombra o retira solo la carpeta application para actualizar.' >&2
  echo 'No elimines su carpeta padre: contiene la base y la sesion.' >&2
  exit 1
fi
if command -v apt-get >/dev/null; then
  sudo apt-get update
  sudo apt-get install -y postgresql postgresql-client
  DISTRO=debian
elif command -v dnf >/dev/null; then
  sudo dnf install -y postgresql-server postgresql nss alsa-lib libXcomposite libXdamage libXrandr mesa-libgbm gtk3
  DISTRO=fedora
elif command -v pacman >/dev/null; then
  sudo pacman -S --needed postgresql nss alsa-lib libxcomposite libxdamage libxrandr mesa gtk3
  DISTRO=arch
else
  echo 'Distribucion no soportada por el instalador. Se admiten Debian/Ubuntu, Fedora y Arch.' >&2
  exit 1
fi
mkdir -p -- "$(dirname -- "$DESTINATION")"
cp -a -- "$SOURCE" "$DESTINATION"
"$DESTINATION/setup/launch.sh" --setup-only
if [[ "$DISTRO" == debian ]]; then
  # Dependencias oficiales de Chromium, sin instalar Python globalmente.
  DATA="$(dirname -- "$DESTINATION")"
  sudo "$DATA/whatsplay/.venv/bin/python" -m playwright install-deps chromium
fi
APPLICATIONS="${XDG_DATA_HOME:-$HOME/.local/share}/applications"
[[ "$APPLICATIONS" = /* ]] || APPLICATIONS="$HOME/.local/share/applications"
mkdir -p -- "$APPLICATIONS"
LAUNCHER="${DESTINATION//\\/\\\\}/setup/launch.sh"
LAUNCHER="${LAUNCHER//\"/\\\"}"
LAUNCHER="${LAUNCHER//%/%%}"
printf '[Desktop Entry]\nType=Application\nName=Trullo\nComment=Gestor de tareas\nExec="%s"\nTerminal=false\nCategories=Office;\n' "$LAUNCHER" > "$APPLICATIONS/trullo.desktop"
echo 'Instalacion terminada. Abri Trullo desde el menu.'
