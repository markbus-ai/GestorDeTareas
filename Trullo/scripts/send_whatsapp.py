#!/usr/bin/env python3
"""
Send one WhatsApp message with WhatsPlay.

Exit code 0 when the message was sent, 1 otherwise. Run it with the
WhatsPlay uv environment:

    uv run --project "$WHATSPLAY_DIR" python scripts/send_whatsapp.py \
        --session "$WHATSPLAY_DIR/whatsapp_session" --to 5491123456789 --message "Hola"

First time only, link the session by scanning the QR in the browser window:

    uv run --project "$WHATSPLAY_DIR" python scripts/send_whatsapp.py \
        --session "$WHATSPLAY_DIR/whatsapp_session" --login-only
"""
import argparse
import asyncio
import sys

from whatsplay import Client
from whatsplay.auth import LocalProfileAuth

# WhatsApp Web needs a moment to deliver the message before the browser closes.
SECONDS_TO_CONFIRM = 8


async def run(args) -> int:
    login_only = args.login_only
    client = Client(auth=LocalProfileAuth(data_dir=args.session), headless=not login_only)
    result = {"ok": False}

    @client.event("on_logged_in")
    async def on_logged_in():
        try:
            if login_only:
                print("Sesión vinculada.", flush=True)
                result["ok"] = True
                return
            result["ok"] = await client.send_message(args.to, args.message, open_via_url=True)
            if result["ok"]:
                await asyncio.sleep(SECONDS_TO_CONFIRM)
            else:
                print(f"No se pudo enviar el mensaje a {args.to}", file=sys.stderr, flush=True)
        except Exception as e:  # noqa: BLE001 - report any failure through the exit code
            print(f"Error al enviar: {e}", file=sys.stderr, flush=True)
        finally:
            await client.stop()

    @client.event("on_qr")
    async def on_qr(_qr):
        if login_only:
            print("Escaneá el QR en la ventana del navegador.", flush=True)
            return
        print("La sesión de WhatsApp no está vinculada; corré con --login-only.", file=sys.stderr, flush=True)
        await client.stop()

    @client.event("on_error")
    async def on_error(error):
        print(f"Error de WhatsPlay: {error}", file=sys.stderr, flush=True)
        await client.stop()

    await client.start()
    return 0 if result["ok"] else 1


def main() -> int:
    parser = argparse.ArgumentParser(description="Envía un mensaje de WhatsApp con WhatsPlay")
    parser.add_argument("--session", required=True, help="Carpeta de la sesión de WhatsApp")
    parser.add_argument("--to", help="Número con código de país, sin '+'")
    parser.add_argument("--message", help="Texto del mensaje")
    parser.add_argument("--login-only", action="store_true", help="Solo vincular la sesión escaneando el QR")
    args = parser.parse_args()

    if not args.login_only and not (args.to and args.message):
        parser.error("Necesitás --to y --message, o --login-only")

    return asyncio.run(run(args))


if __name__ == "__main__":
    sys.exit(main())
