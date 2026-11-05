# Trullo — Gestor de Tareas

App de escritorio hecha en **Java + Swing** para organizar tu día. Tiene tareas, calendario con recordatorios y notas tipo post-its. Todo con tema claro/oscuro y un diseño piola sin complicarla.

Hecha para la facu pero usable posta, no es el tipico gestor feo de consola.

## ✨ Que tiene

- **My Tasks** — creas tareas, le pones deadline opcional, filtras por Todas / Pendientes / Completadas. Al marcar como hecha se tacha y a los 1s desaparece.
- **Calendario** — vista mensual con navegación `< >`, marca hoy y el día seleccionado. Los días con recordatorios muestran puntito naranja. Click en un día → ves/agregas recordatorios a la derecha.
- **Notas** — cards de colores que se expanden al tocarlas. Buscador arriba que filtra al tipear. Botón `+` verde para crear.
- **Proyectos** — crear proyectos, cambiar su estado y eliminarlos. Cada tarea se asigna a un proyecto desde su tarjeta o al crearla.
- **Configuración** — switch de tema (oscuro/claro) con FlatLaf. Todo se actualiza en vivo.

## 🛠️ Stack

- Java 26, Maven
- Swing + [FlatLaf 3.6](https://www.formdev.com/flatlaf/) (look and feel moderno)
- Arquitectura simple: `TrulloApp` arma la ventana y el `MenuLateral` va cambiando el panel central

## 📁 Estructura del proyecto

```
GestorDeTareas/
├── README.md
└── Trullo/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/trullo/
        │   │   ├── TrulloApp.java              # main, arma la ventana y navega entre secciones
        │   │   ├── frontend/                   # toda la UI (antes ui)
        │   │   │   ├── ThemeManager.java       # paleta dark/light y listeners de tema
        │   │   │   ├── ConstantesUI.java       # medidas y helper paintRoundRect
        │   │   │   ├── ModernScrollBarUI.java  # scrollbar fino y redondeado
        │   │   │   ├── MenuLateral.java        # sidebar izquierda con navegación
        │   │   │   ├── PanelTareas.java        # lista + filtros + dialog nueva tarea
        │   │   │   ├── CalendarioMensual.java  # grilla del mes, selección y puntitos
        │   │   │   ├── PanelCalendario.java    # wrapper calendario + recordatorios
        │   │   │   ├── PanelRecordatorios.java # lista del día + input agregar
        │   │   │   └── PanelNotas.java         # cards de colores + buscador
        │   │   ├── recursos/                   # recursos gráficos (antes util)
        │   │   │   └── EmojiIcon.java          # wrapper para pintar emojis en Swing
        │   │   ├── model/                      # modelos de dominio (vacío, para expandir)
        │   │   ├── entity/                     # entidades de persistencia
        │   │   ├── dao/                        # acceso a datos directo
        │   │   ├── repository/                 # abstracción de persistencia
        │   │   ├── service/                    # lógica de negocio
        │   │   ├── controller/                 # coordina frontend <-> service
        │   │   ├── dto/                        # objetos de transferencia
        │   │   ├── config/                     # configuración (DB, props)
        │   │   └── exception/                  # excepciones custom
        │   └── resources/                      # recursos estáticos (vacío)
        └── test/java/com/trullo/              # tests (vacío)
```

> `frontend` y `recursos` ya tienen código, el resto (`model`, `dao`, `service`, etc.) están creados vacíos con `.gitkeep` para que la estructura quede lista para escalar a una arquitectura en capas cuando toque meter persistencia.

## 🚀 Como correrlo

```bash
# clonar y entrar
git clone <url>
cd GestorDeTareas/Trullo

# compilar
mvn compile

# ejecutar (necesitás JDK 26)
mvn exec:java
# o
java -cp target/classes com.trullo.TrulloApp
```

Si tenés otro JDK default, forzá el 26:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-26.0.2"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn compile
```

## 🔔 Recordatorios

Un recordatorio (`reminders`) tiene uno o más destinos (`reminder_targets`): cada destino es un canal (`whatsapp`, `email`, ...) y una dirección dentro de ese canal. Así un mismo recordatorio puede salir por WhatsApp y por email a la vez.

Al arrancar la app se levanta un `ReminderScheduler` en segundo plano para los canales locales, como email. Cada minuto busca los destinos vencidos que todavía no se enviaron y marca como enviados (`sent_at`) los que salieron bien. WhatsApp se programa en el servidor al crear el recordatorio y se omite en este proceso. Los que fallan quedan pendientes y se reintentan en la próxima pasada. Si un destino usa un canal que no está configurado, se saltea.

Cada canal se activa solo cuando está configurado en el `.env`. Si no hay ninguno, el scheduler no arranca.

### Agregar un canal nuevo

Los canales se descubren con `ServiceLoader`, así que sumar uno (por ejemplo Telegram) no toca ninguna clase existente:

1. Una clase que implemente `Notifier` (`channel()`, `validate(address)`, `send(address, message)`).
2. Una fábrica que implemente `NotifierFactory`, con constructor público sin parámetros, que devuelva el notificador si su configuración está en el `.env` o `Optional.empty()` si no.
3. Una línea con el nombre completo de la fábrica en `src/main/resources/META-INF/services/com.trullo.interfaces.notification.NotifierFactory`.

### WhatsApp

Se programa en Truello-Api al crear el recordatorio. Configurar en `Trullo/.env`:

```dotenv
WHATSAPP_API_URL=http://31.97.160.86/trullo
API_KEY=clave-configurada-en-el-servidor
WHATSAPP_API_ZONE=America/Argentina/Buenos_Aires
```

La URL es la base, sin `/wsp-queues`. Java hace `POST /wsp-queues` con
`X-API-Key` y los campos `phone`, `tarea`, `fecha` (`dd/MM/yyyy`) y `hora`
(`HH:mm`). El horario elegido se convierte de la zona del escritorio a la zona
configurada del servidor. La API admite solamente horarios futuros, con precisión
de minutos. El worker del servidor envía aunque el escritorio esté cerrado.

No se necesitan Python, uv, WhatsPlay, Chrome ni QR en el escritorio. La cuenta
emisora y su vinculación quedan en el servidor. La plantilla se administra en
Truello-Api. El planificador local omite los canales `ScheduledNotifier`, para
no encolar dos veces el mismo recordatorio. El correo sigue enviándose localmente.

La confirmación de la API significa **programado**, no entregado: no se marca
`sent_at` al encolar. El ID de cola confirmado se informa en el log; esta versión
no sincroniza el estado remoto con la base local. Los recordatorios WhatsApp
anteriores a esta integración no se programan automáticamente en el servidor.

La API actual no permite cancelar ni modificar un envío programado. Eliminar
un recordatorio (o la tarea asociada) localmente no cancela su envío remoto.
Si hay timeout o una respuesta ambigua, consultar la cola antes de volver a
crear el recordatorio: no hay idempotencia en la API y el cliente no reintenta
el POST automáticamente.

### Email

Se envía por SMTP con STARTTLS. Se activa cuando `SMTP_HOST` y `SMTP_USER` tienen valor:

```
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587                 # opcional, 587 por defecto
SMTP_USER=tu-cuenta@gmail.com
SMTP_PASSWORD=                # en Gmail, una contraseña de aplicación
SMTP_FROM=                    # opcional, usa SMTP_USER por defecto
```

Gmail no acepta la contraseña normal de la cuenta: hay que generar una contraseña de aplicación en *Cuenta de Google > Seguridad > Contraseñas de aplicaciones*.

## 🎨 Temas

`ThemeManager` guarda dos paletas (`D_*` y `L_*`) y expone `fondo()`, `texto()`, `tarjeta()`, etc. Los paneles se suscriben con `ThemeManager.onThemeChange(() -> repaint())`. El toggle de Configuración hace `ThemeManager.toggle()`.

## 📝 Comentarios

Todo el código está comentado con onda estudiante informal, para que se entienda qué hace cada cosa sin sonar a doc generada por IA.

## 📌 Roadmap

- [ ] Persistencia real (archivos / SQLite) via `repository` + `dao`
- [ ] Editar / borrar tareas y notas
- [x] Recordatorios por WhatsApp
- [x] Recordatorios por email
- [ ] Exportar a JSON

---
Hecho con Java y mucho `SwingUtilities.invokeLater` 😅
