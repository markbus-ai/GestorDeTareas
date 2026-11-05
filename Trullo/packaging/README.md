# Setup de Trullo: Windows y Linux x64

Este empaquetado corresponde a la rama con Python/WhatsPlay. No usa Truello-Api.
El instalador incluye Java y uv. En Windows incluye además los binarios de
PostgreSQL y el runtime de Microsoft; en Linux usa PostgreSQL de la distribución.
Python 3.12, WhatsPlay fijado en 2.6.11 y Chromium se descargan en la primera
preparación, por lo que hace falta Internet. No es un instalador offline.

## Compilar en Linux

Desde `Trullo`, con JDK 25 (java, javac, jlink, jpackage), Maven, Python 3 y uv en PATH:

```sh
python3 packaging/build.py
```

El build toma la sesión detenida de `WHATSPLAY_DIR/whatsapp_session` y los campos
SMTP del `.env` local del administrador. Puede elegirse otra fuente:

```sh
python3 packaging/build.py --sender-session /ruta/perfil --sender-env /ruta/mail.env
```

Se requiere SMTP_HOST, SMTP_USER y SMTP_PASSWORD. Nunca se copian DB_PASSWORD ni
otras claves del `.env` del desarrollador. Cerrar WhatsApp antes de construir.

Resultado: `target/dist/Trullo-1.0.0-linux-x64.tar.gz`.
Para distribuir a varias distribuciones, compilar en Ubuntu 22.04 (workflow
incluido); el artefacto generado localmente depende de la compatibilidad de
libc del JDK y uv del equipo de compilación. Solo se soporta x86-64.

El build usa un fat JAR con servicios fusionados, Java incluido e icono. Ejecuta
una comprobación de JDBC, SMTP, TLS, lectura de configuración instalada e imágenes
sobre el runtime distribuido, sin abrir la interfaz ni enviar mensajes.

## Compilar en Windows

Se compila **en Windows**, con JDK 25, Maven, Python 3, uv e Inno Setup 6 en el
equipo de compilación. Descargar y verificar el ZIP de binarios PostgreSQL x64
oficial de EDB, extraerlo, y descargar `vc_redist.x64.exe` oficial de Microsoft.
La carpeta PostgreSQL indicada debe contener `bin`, `lib` y `share`, nunca una
carpeta con datos personales.

```powershell
python packaging/build.py --postgres-dir C:\pgsql --vc-redist C:\descargas\vc_redist.x64.exe --iscc "C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
```

Resultado: `target/dist/TrulloSetup-1.0.0-windows-x64.exe`.
Windows 10/11 x64. El setup pide elevación para instalar los componentes de
Microsoft y copiar la aplicación a Program Files. La preparación de Python y base de datos corre con el usuario original, no con la cuenta elevada.
`Trullo.exe` y el acceso del menú Inicio ejecutan la preparación y arrancan
PostgreSQL antes de abrir la interfaz. `Trullo-java.exe` es el launcher Java
interno; solo lo ejecuta el bootstrap, que espera su cierre para detener la base.
No se requiere un servicio PostgreSQL instalado ni una contraseña administrativa
existente. Java tampoco se instala globalmente.

La compilación de Windows debe comprobarse en un equipo/VM Windows. Una ejecución
de tests en Linux no demuestra que el EXE instale correctamente en Windows.

## GitHub Actions

En la raíz del repositorio está `.github/workflows/build-installers.yml`. Es
manual: Actions → Build Trullo installers → Run workflow. Requiere la URL HTTPS
del ZIP de PostgreSQL de `get.enterprisedb.com/postgresql/` y su SHA-256 verificado.
Requiere también los secretos `TRULLO_SENDER_LINUX_URL` y
`TRULLO_SENDER_WINDOWS_URL`: URLs HTTPS privadas/temporales de los perfiles
emisores exportados con `python3 packaging/export_sender.py`. El ZIP debe
contener `session/` y `mail.env`. Es información sensible; no subirla al repositorio.
Genera los dos artefactos privados sin publicarlos como releases. Comprueba el hash del ZIP
y la firma Authenticode de Microsoft antes de construir el setup Windows.

## Instalar en Linux

En el equipo destino:

```sh
tar -xzf Trullo-1.0.0-linux-x64.tar.gz
cd Trullo
./install.sh
```

Ejecutarlo con el usuario normal. Pedirá sudo para instalar PostgreSQL y las
bibliotecas del navegador. Hay ramas para Debian/Ubuntu, Fedora y Arch; se necesita
una sesión gráfica. No ejecuta `initdb` sobre el PostgreSQL de la distribución ni
usa su servicio global. La instalación del paquete puede arrancarlo según las políticas de la distribución; Trullo no lo detiene ni lo reinicializa. En Debian/Ubuntu usa el comando oficial
`playwright install-deps chromium` para instalar bibliotecas del navegador.

Se crea la entrada del menú de Trullo. No hay pantallas de QR ni configuración SMTP. La instalación
por usuario queda en `~/.local/share/trullo/application` (o XDG_DATA_HOME).
Para actualizar esta primera versión, cerrar Trullo y retirar/renombrar **solo**
`application`, luego instalar el paquete nuevo. No borrar su carpeta padre.
El instalador se detiene si encuentra una instalación anterior para no sobrescribir
archivos de una aplicación abierta.

## Datos y primera ejecución

| Sistema | Configuración | Datos |
| --- | --- | --- |
| Windows | `%APPDATA%\Trullo\.env` | `%LOCALAPPDATA%\Trullo` |
| Linux | `$XDG_CONFIG_HOME/trullo/.env` o `~/.config/trullo/.env` | `$XDG_DATA_HOME/trullo` o `~/.local/share/trullo` |

Dentro de los datos quedan `postgres/`, `database-state.json`, `postgres.log`,
`whatsplay/`, `browsers/`, `python/` y `uv-cache/`. Las contraseñas se generan al azar
y se conservan al volver a preparar. No se incluye la configuración de base de desarrollo ni datos de ejemplo. El paquete privado sí incluye una copia de la sesión emisora y las credenciales SMTP del administrador, porque se usan como emisor fijo. No distribuirlo públicamente ni subirlo a Git.

La base privada escucha solo en 127.0.0.1:54329. Si el puerto está ocupado, falla
con diagnóstico en `postgres.log`; no detiene ni reutiliza otro servidor. La app
usa un rol propio `trullo_app`. La preparación inicial aplica enums y tablas en
una transacción y deja un marcador de schema. Una segunda preparación conserva
las notas y tareas existentes. Si hay tablas desconocidas o una versión de schema
incompatible, se detiene sin borrar nada. No incluye migraciones de versiones
futuras ni actualización automática de un cluster a otra versión mayor de PostgreSQL.

Una sola instancia de Trullo o de la vinculación puede estar abierta por usuario.
El launcher mantiene el PostgreSQL privado mientras se usa la app y lo detiene
al finalizar. Una terminación abrupta del launcher puede requerir volver a abrirlo
para recuperar/cerrar su cluster; nunca se debe eliminar manualmente el cluster.

El usuario no vincula WhatsApp ni configura correo. El administrador prepara la
sesión y SMTP al compilar. La sesión se copia solo si no existe una en el destino,
y el correo se incorpora solo al crear la configuración. Una configuración
existente siempre se conserva. El proceso es headless: si la sesión no sirve,
falla el envío sin abrir navegador, pedir QR ni iniciar un servidor QR.

**Copiar un perfil Chromium no garantiza que siga autenticado en otra máquina o
en otro sistema operativo.** Debe validarse el perfil en Windows y Linux antes
de distribuirlo. Si falla, el administrador debe preparar una sesión de la misma
cuenta en ese entorno; el usuario final no recibe un flujo de vinculación.
El programa debe permanecer abierto para enviar los recordatorios.

En Linux también se puede abrir con `./bin/trullo` (o `./bin/Trullo`) desde
la carpeta del paquete. Ambos ejecutables públicos inician la preparación y
PostgreSQL antes de abrir la interfaz, y detienen la base al cerrar la aplicación.
`bin/Trullo-java` es el launcher interno y no debe abrirse directamente.

Para reparación o vinculación manual:

```sh
# Linux, desde la instalación
./setup/launch.sh --setup-only --repair
```

```powershell
# Windows, desde la instalación
powershell -NoProfile -ExecutionPolicy Bypass -File .\setup\launch.ps1 -SetupOnly -Repair
```

Desinstalar Windows conserva configuración y datos del usuario. En Linux se puede
retirar `application` y las dos entradas `trullo*.desktop` del directorio de
aplicaciones del usuario. La eliminación completa de datos es una operación
separada y explícita.

## Tests del instalador

```sh
PYTHONDONTWRITEBYTECODE=1 python3 -m unittest discover -s packaging/tests -v
# Prueba real de schema en un cluster temporal aislado, con binarios PostgreSQL:
PYTHONDONTWRITEBYTECODE=1 TRULLO_SETUP_TEST_POSTGRES=1 python3 -m unittest discover -s packaging/tests -v
```

La prueba de PostgreSQL no toca `.env`, bases ni sesiones del gestor: crea un
cluster temporal en /tmp, verifica instalación/reinstalación y conservación de
una nota, rechaza un schema desconocido y detiene el cluster al terminar.
No vincula WhatsApp ni envía mensajes.

Fuentes de herramientas:
- https://docs.oracle.com/en/java/javase/25/jpackage/packaging-overview.html
- https://www.postgresql.org/download/windows/
- https://www.enterprisedb.com/download-postgresql-binaries
- https://docs.astral.sh/uv/guides/install-python/
- https://playwright.dev/python/docs/browsers

## Desarrollo sin instalador

`mvn compile`, `mvn test` y `mvn compile exec:java` siguen funcionando normalmente.
Solo `mvn -Pinstaller package` activa el empaquetado especial. El `.env` local
se sigue usando en desarrollo y el instalador no lo modifica.
