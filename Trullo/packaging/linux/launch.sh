#!/usr/bin/env bash
set -euo pipefail
TRULLO_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
TRULLO_DATA="${XDG_DATA_HOME:-$HOME/.local/share}/trullo"
[[ "$TRULLO_DATA" = /* ]] || TRULLO_DATA="$HOME/.local/share/trullo"
export UV_PYTHON_INSTALL_DIR="$TRULLO_DATA/python"
export UV_CACHE_DIR="$TRULLO_DATA/uv-cache"
export PLAYWRIGHT_BROWSERS_PATH="$TRULLO_DATA/browsers"
exec "$TRULLO_ROOT/tools/uv" run --no-project --python 3.12 python \
  "$TRULLO_ROOT/setup/bootstrap.py" --home "$TRULLO_ROOT" "$@"
