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
  echo 'No elimines su carpeta padre: contiene la base de datos.' >&2
  exit 1
fi

if command -v apt-get >/dev/null 2>&1; then
  sudo apt-get update
  sudo apt-get install -y postgresql postgresql-client \
    libx11-6 libxext6 libxrender1 libxi6 libxtst6 libxfixes3 fontconfig
elif command -v dnf >/dev/null 2>&1; then
  sudo dnf install -y postgresql-server postgresql \
    libX11 libXext libXrender libXi libXtst libXfixes fontconfig
elif command -v pacman >/dev/null 2>&1; then
  sudo pacman -S --needed postgresql \
    libx11 libxext libxrender libxi libxtst libxfixes fontconfig
else
  echo 'Distribucion no soportada por el instalador. Se admiten Debian/Ubuntu, Fedora y Arch.' >&2
  exit 1
fi

mkdir -p -- "$(dirname -- "$DESTINATION")"
cp -a -- "$SOURCE" "$DESTINATION"
"$DESTINATION/bin/Trullo" --setup-only --home "$DESTINATION"

APPLICATIONS="${XDG_DATA_HOME:-$HOME/.local/share}/applications"
[[ "$APPLICATIONS" = /* ]] || APPLICATIONS="$HOME/.local/share/applications"
mkdir -p -- "$APPLICATIONS"
LAUNCHER="${DESTINATION//\\/\\\\}/bin/Trullo"
LAUNCHER="${LAUNCHER//\"/\\\"}"
LAUNCHER="${LAUNCHER//%/%%}"
printf '[Desktop Entry]\nType=Application\nName=Trullo\nComment=Gestor de tareas\nExec="%s"\nTerminal=false\nCategories=Office;\n' "$LAUNCHER" > "$APPLICATIONS/trullo.desktop"
echo 'Instalacion terminada. Abri Trullo desde el menu.'
