# Trullo

Gestor de tareas, proyectos, notas y recordatorios.

## Linux

Para instalar, abrí una terminal en esta carpeta y ejecutá:

```sh
./install.sh
```

Después abrí Trullo desde el menú de aplicaciones. También podés ejecutarlo
desde esta carpeta con:

```sh
./bin/trullo
```

## Windows

Instalá Trullo con el setup y abrilo desde el menú Inicio o con `Trullo.exe`.
El ejecutable inicia la base de datos antes de abrir la aplicación.

## Recordatorios

Necesitás Internet para la preparación inicial y para enviar mensajes.
Mantené Trullo abierto para que se envíen los recordatorios.

## Si no inicia

Con Trullo cerrado, abrí una terminal en esta carpeta y ejecutá la reparación.
Necesita Internet y conserva tus tareas, notas y configuración.

Linux:

```sh
./bin/trullo --setup-only --repair
```

Windows (PowerShell):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\setup\launch.ps1 -SetupOnly -Repair
```

`--repair` vuelve a preparar las dependencias de WhatsApp; `--setup-only`
termina la preparación sin abrir Trullo. Después abrí la aplicación normalmente.

En Linux, ejecutá `./bin/trullo` desde una terminal para ver el error.
En Windows, revisá el mensaje de la consola de preparación.
No borres las carpetas de datos para intentar repararlo.
