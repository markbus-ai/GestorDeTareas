package com.trullo.interfaces.notification;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Optional;

/**
 * Crea un {@link Notifier} a partir de la configuración de la aplicación.
 *
 * <p>Las fábricas se descubren con {@link java.util.ServiceLoader}: cada implementación necesita
 * un constructor público sin parámetros y una línea con su nombre completo en
 * {@code META-INF/services/com.trullo.interfaces.notification.NotifierFactory}.
 */
public interface NotifierFactory {
    /**
     * Crea el notificador del canal si está configurado.
     *
     * @param config las variables de configuración, normalmente las del archivo {@code .env}
     * @return el notificador listo para usar, o vacío si el canal no está configurado
     */
    Optional<Notifier> create(Dotenv config);
}
