package com.trullo.notification.whatsapp;

import com.trullo.interfaces.notification.Notifier;
import com.trullo.interfaces.notification.NotifierFactory;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.ZoneId;
import java.util.Optional;

/** Activa WhatsApp cuando están configuradas la URL y la clave de Truello-Api. */
public class WhatsAppNotifierFactory implements NotifierFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Notifier> create(Dotenv config) {
        String url = config.get("WHATSAPP_API_URL", "").trim();
        String key = config.get("API_KEY", "").trim();
        if (url.isEmpty() || key.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new WhatsAppApiNotifier(url, key,
                ZoneId.of(config.get("WHATSAPP_API_ZONE", "America/Argentina/Buenos_Aires"))));
    }
}
