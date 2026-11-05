import com.trullo.core.AppConfig;
import com.trullo.interfaces.notification.NotifierFactory;
import com.trullo.notification.whatsapp.WhatsAppNotifierFactory;
import com.trullo.setup.SetupMain;
import jakarta.mail.Session;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;

/** Comprobacion del Java distribuido sin conexiones, interfaz ni envios reales. */
public class RuntimeProbe {
    public static void main(String[] args) throws Exception {
        if (!"runtime-probe".equals(AppConfig.load().get("DB_NAME"))) {
            throw new AssertionError("No se cargo la configuracion instalada");
        }
        if (new WhatsAppNotifierFactory().create(AppConfig.load()).isEmpty()) {
            throw new AssertionError("WhatsApp no fue descubierto");
        }
        if (ServiceLoader.load(NotifierFactory.class).stream().count() != 2) {
            throw new AssertionError("Se perdieron los proveedores de notificaciones");
        }
        Class.forName(SetupMain.class.getName());
        DriverManager.getDriver("jdbc:postgresql://127.0.0.1/unused");
        try (var transport = Session.getInstance(new Properties()).getTransport("smtp")) {
            if (transport == null) {
                throw new AssertionError("SMTP no disponible");
            }
        }
        SSLContext.getDefault().createSSLEngine();
        String month = DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("es-ES"))
                .format(LocalDate.of(2026, 11, 5));
        if (!"noviembre".equals(month)) {
            throw new AssertionError("Faltan datos de idioma en el runtime: " + month);
        }
        try (var icon = AppConfig.class.getResourceAsStream("/com/trullo/recursos/icono.png")) {
            if (icon == null || ImageIO.read(icon) == null) {
                throw new AssertionError("Icono no empaquetado");
            }
        }
        System.out.println("Runtime OK: configuracion, API, PostgreSQL JDBC, SMTP, TLS, idioma, imagenes y setup.");
    }
}
