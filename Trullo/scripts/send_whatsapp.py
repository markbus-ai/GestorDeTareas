#!/usr/bin/env python3
"""Envía con la sesión emisora preparada de Trullo, siempre sin navegador visible.

--check-session comprueba la sesión sin enviar mensajes. Si la sesión deja de ser
válida, termina con error: no pide QR ni inicia un servidor para mostrarlo.
"""
import argparse
from contextlib import contextmanager
import asyncio
import sys

from whatsplay import Client
from whatsplay.auth import LocalProfileAuth

# WhatsApp Web needs a moment to deliver the message before the browser closes.
SECONDS_TO_CONFIRM = 8


@contextmanager
def without_qr_ui():
    from whatsplay import state_manager
    names = ("show_qr_window", "update_qr_code", "close_qr_window")
    original = {name: getattr(state_manager, name) for name in names}
    try:
        for name in names:
            setattr(state_manager, name, lambda *args, **kwargs: None)
        yield
    finally:
        for name, callback in original.items():
            setattr(state_manager, name, callback)


async def run(args) -> int:
    login_only = args.login_only or args.check_session
    client = Client(auth=LocalProfileAuth(data_dir=args.session), headless=True)
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
        print("La sesion emisora no esta disponible. Contacta al administrador de Trullo.",
              file=sys.stderr, flush=True)
        await client.stop()

    @client.event("on_error")
    async def on_error(error):
        print(f"Error de WhatsPlay: {error}", file=sys.stderr, flush=True)
        await client.stop()

    # WhatsPlay inicia por defecto un servidor QR; esta instalación usa una sesión
    # previamente preparada y nunca debe abrir un flujo de vinculación al usuario.
    with without_qr_ui():
        await client.start()
    return 0 if result["ok"] else 1


def main() -> int:
    parser = argparse.ArgumentParser(description="Envía un mensaje de WhatsApp con WhatsPlay")
    parser.add_argument("--session", required=True, help="Carpeta de la sesión de WhatsApp")
    parser.add_argument("--to", help="Número con código de país, sin '+'")
    parser.add_argument("--message", help="Texto del mensaje")
    parser.add_argument("--login-only", action="store_true", help="Alias de --check-session; no abre QR")
    parser.add_argument("--check-session", action="store_true", help="Comprueba la sesion emisora sin enviar")
    args = parser.parse_args()

    if not (args.login_only or args.check_session) and not (args.to and args.message):
        parser.error("Necesitás --to y --message, o --login-only")

    return asyncio.run(run(args))


if __name__ == "__main__":
    sys.exit(main())
