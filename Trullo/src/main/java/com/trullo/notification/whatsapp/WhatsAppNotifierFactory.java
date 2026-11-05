package com.trullo.notification.whatsapp;

import com.trullo.interfaces.notification.Notifier;
import com.trullo.interfaces.notification.NotifierFactory;
import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Crea el canal de WhatsApp cuando {@code WHATSPLAY_DIR} está configurado.
 *
 * <p>{@code WHATSPLAY_DIR} es la carpeta de WhatsPlay, con su proyecto {@code uv} y su
 * {@code whatsapp_session}. Si está vacía, el canal queda desactivado.
 */
public class WhatsAppNotifierFactory implements NotifierFactory {
    private static final String WHATSPLAY_DIR = "WHATSPLAY_DIR";

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Notifier> create(Dotenv config) {
        String whatsplayDir = config.get(WHATSPLAY_DIR, "").trim();
        if (whatsplayDir.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(WhatsAppNotifier.forDirectory(Path.of(whatsplayDir),
                com.trullo.core.AppConfig.whatsAppScript(), config.get("UV_EXECUTABLE", "uv")));
    }
}
