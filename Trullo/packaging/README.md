# Setup de Trullo: Windows y Linux x64

Este empaquetado corresponde a la rama que programa WhatsApp en Truello-Api. El
escritorio no necesita Python, uv, WhatsPlay, Chromium, QR ni sesion emisora. La
aplicacion sigue siendo Java + Swing y usa una base PostgreSQL privada.

El instalador incluye un runtime Java. En Windows incluye tambien los binarios
oficiales de PostgreSQL y el runtime de Microsoft; en Linux instala PostgreSQL de
la distribucion. La configuracion privada de `WHATSAPP_API_URL`, `API_KEY` y SMTP
se incorpora al instalar y queda en la carpeta de configuracion del usuario.

## Compilar en Linux

Desde `Trullo`, con JDK 25 (`java`, `javac`, `jlink`, `jpackage`), Maven y Python 3
en PATH:

```sh
python3 packaging/build.py
```

El build toma `WHATSAPP_API_URL`, `API_KEY`, `WHATSAPP_API_ZONE` y los campos SMTP
del `.env` local. Se puede indicar otro archivo:

```sh
python3 packaging/build.py --config-env /ruta/privado.env
```

Se requiere `WHATSAPP_API_URL` y `API_KEY`. Los valores SMTP son opcionales, pero
si se incluyen deben ser coherentes. Nunca se copian `DB_HOST`, `DB_USER` ni
`DB_PASSWORD` del archivo de build: la base privada genera sus propias
credenciales en cada instalacion.

Resultado: `target/dist/Trullo-1.0.0-linux-x64.tar.gz` y
`target/dist/SHA256SUMS`.

El build usa un fat JAR con servicios fusionados, Java incluido e icono. Antes de
empaquetar ejecuta una comprobacion del runtime distribuido: lectura de la
configuracion instalada, descubrimiento de los canales, JDBC de PostgreSQL, SMTP,
TLS, imagenes y clase de preparacion.

Solo se soporta x86-64. Para distribuir a varias distribuciones conviene compilar
en Ubuntu 22.04; el artefacto local depende de la compatibilidad de libc del JDK
del equipo de compilacion.

## Compilar en Windows

Se compila **en Windows**, con JDK 25, Maven, Python 3 e Inno Setup 6. Descargar y
verificar el ZIP oficial de binarios PostgreSQL x64 de EDB, extraerlo y descargar
`vc_redist.x64.exe` oficial de Microsoft. La carpeta PostgreSQL indicada debe
contener `bin`, `lib` y `share`, nunca una carpeta con datos personales.

```powershell
python packaging/build.py --postgres-dir C:\pgsql --vc-redist C:\descargas\vc_redist.x64.exe --iscc "C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
```

Resultado: `target/dist/TrulloSetup-1.0.0-windows-x64.exe` y
`target/dist/SHA256SUMS`.

Windows 10/11 x64. El setup pide elevacion para instalar los componentes de
Microsoft y copiar la aplicacion a Program Files. La preparacion de la base corre
con el usuario original, no con la cuenta elevada. Java tampoco se instala
globalmente.

La compilacion de Windows debe probarse en un equipo o VM Windows. Una ejecucion
de tests en Linux no demuestra que el EXE instale correctamente en Windows.

## GitHub Actions

En la raiz del repositorio esta `.github/workflows/build-installers.yml`. Es
manual: Actions -> Build Trullo installers -> Run workflow.

Requiere:

- Inputs `postgres_url` y `postgres_sha256`: URL HTTPS del ZIP oficial de
  PostgreSQL de `get.enterprisedb.com/postgresql/` y su SHA-256 verificado.
- Secreto `TRULLO_PRIVATE_ENV`: contenido del `.env` privado con
  `WHATSAPP_API_URL`, `API_KEY`, `WHATSAPP_API_ZONE` y los datos SMTP.

El workflow genera los dos artefactos privados sin publicarlos como releases y
sube tambien `SHA256SUMS`. Comprueba el hash del ZIP de PostgreSQL y la firma
Authenticode de Microsoft antes de construir el setup Windows.

## Instalar en Linux

En el equipo destino:

```sh
tar -xzf Trullo-1.0.0-linux-x64.tar.gz
cd Trullo
./install.sh
```

Ejecutarlo con el usuario normal. Pedira sudo para instalar PostgreSQL y las
bibliotecas de escritorio que necesita Java. Hay ramas para Debian/Ubuntu, Fedora
y Arch; se necesita una sesion grafica.

La instalacion por usuario queda en `~/.local/share/trullo/application` (o
`XDG_DATA_HOME`). Se crea la entrada del menu de Trullo. No hay pantallas de QR ni
configuracion SMTP.

Para actualizar, cerrar Trullo y retirar/renombrar **solo** `application`, luego
instalar el paquete nuevo. No borrar su carpeta padre: contiene la base y la
configuracion.

## Datos y primera ejecucion

| Sistema | Configuracion | Datos |
| --- | --- | --- |
| Windows | `%APPDATA%\Trullo\.env` | `%LOCALAPPDATA%\Trullo` |
| Linux | `$XDG_CONFIG_HOME/trullo/.env` o `~/.config/trullo/.env` | `$XDG_DATA_HOME/trullo` o `~/.local/share/trullo` |

Dentro de los datos quedan `postgres/`, `database-state.json`, `postgres.log` y
`setup.lock`. Las contrasenas de la base se generan al azar y se conservan al
volver a preparar.

La base privada escucha solo en `127.0.0.1:54329`. Si el puerto esta ocupado,
falla con diagnostico en `postgres.log`; no detiene ni reutiliza otro servidor. La
aplicacion usa el rol propio `trullo_app`. La preparacion inicial aplica enums y
tablas en una transaccion y deja un marcador de schema. Una segunda preparacion
conserva las notas y tareas existentes. Si hay tablas desconocidas o una version
de schema incompatible, se detiene sin borrar nada.

Una sola instancia de Trullo puede estar abierta por usuario. El launcher mantiene
el PostgreSQL privado mientras se usa la aplicacion y lo detiene al finalizar. Una
terminacion abrupta puede requerir volver a abrirlo para recuperar/cerrar su
cluster; nunca se debe eliminar manualmente el cluster.

El paquete privado incluye la URL y la clave de Truello-Api y las credenciales
SMTP del administrador. Cualquier persona que reciba el instalador puede
extraerlas: distribuirlo solo de forma privada, no subirlo a Git ni publicarlo.
Si la clave se filtra, rotarla en Truello-Api y volver a compilar.

La confirmacion de la API significa **programado**, no entregado. El worker del
servidor envia aunque el escritorio este cerrado. El correo sigue enviandose
localmente, por lo que Trullo debe estar abierto para esos envios.

Para reparacion o vinculacion manual:

```sh
# Linux, desde la instalacion
~/.local/share/trullo/application/bin/Trullo --setup-only --repair
```

```powershell
# Windows, desde la instalacion
& "$env:ProgramFiles\Trullo\Trullo.exe" --setup-only --repair
```

En Windows el acceso directo "Reparar preparacion" del menu ya ejecuta esa orden.
Desinstalar Windows conserva configuracion y datos del usuario. En Linux se puede
retirar `application` y la entrada `trullo.desktop`. La eliminacion completa de
datos es una operacion separada y explicita.

## Tests

```sh
mvn test
# Prueba real de schema en un cluster temporal aislado, con binarios PostgreSQL:
TRULLO_SETUP_TEST_POSTGRES=1 mvn test
```

La prueba de PostgreSQL no toca el `.env`, las bases ni los datos reales: crea un
cluster temporal, verifica instalacion/reinstalacion y conservacion de una nota,
rechaza un schema desconocido y detiene el cluster al terminar. No envia mensajes
ni abre la interfaz.

## Desarrollo sin instalador

`mvn compile`, `mvn test` y `mvn compile exec:java` siguen funcionando. Solo
`mvn -Pinstaller package` activa el empaquetado especial. El `.env` local se sigue
usando en desarrollo y el instalador no lo modifica.

Fuentes de herramientas:

- https://docs.oracle.com/en/java/javase/25/jpackage/packaging-overview.html
- https://www.postgresql.org/download/windows/
- https://www.enterprisedb.com/download-postgresql-binaries
