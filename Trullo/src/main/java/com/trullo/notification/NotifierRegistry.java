package com.trullo.notification;

import com.trullo.exception.ValidationException;
import com.trullo.interfaces.notification.Notifier;
import com.trullo.interfaces.notification.NotifierFactory;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Conjunto de canales de notificación disponibles, indexados por su id.
 *
 * <p>Permite encontrar el {@link Notifier} de un canal sin conocer las clases concretas, así el
 * resto de la aplicación no cambia cuando se agrega un canal nuevo.
 */
public class NotifierRegistry {
    private final Map<String, Notifier> notifiersByChannel = new HashMap<>();

    /**
     * Crea el registro con los notificadores indicados.
     *
     * @param notifiers los notificadores disponibles
     * @throws IllegalStateException si dos notificadores usan el mismo id de canal
     */
    public NotifierRegistry(Collection<Notifier> notifiers) {
        for (Notifier notifier : notifiers) {
            register(notifier);
        }
    }

    /**
     * Crea el registro con todos los canales configurados, descubiertos con {@link ServiceLoader}.
     *
     * @param config las variables de configuración que reciben las fábricas
     * @return el registro con los canales que están configurados; vacío si no hay ninguno
     * @throws IllegalStateException si dos canales usan el mismo id
     */
    public static NotifierRegistry fromServiceLoader(Dotenv config) {
        return new NotifierRegistry(ServiceLoader.load(NotifierFactory.class).stream()
                .map(ServiceLoader.Provider::get)
                .flatMap(factory -> factory.create(config).stream())
                .toList());
    }

    /**
     * Busca el notificador de un canal.
     *
     * @param channel el id del canal
     * @return el notificador, o vacío si el canal no está disponible
     */
    public Optional<Notifier> find(String channel) {
        return Optional.ofNullable(notifiersByChannel.get(channel));
    }

    /**
     * Indica si no hay ningún canal disponible.
     *
     * @return {@code true} si el registro no tiene notificadores
     */
    public boolean isEmpty() {
        return notifiersByChannel.isEmpty();
    }

    /**
     * Valida que el canal exista y que la dirección sea válida para ese canal.
     *
     * @param channel el id del canal
     * @param address la dirección de destino
     * @throws ValidationException si el canal no está disponible o la dirección no es válida
     */
    public void validate(String channel, String address) {
        Notifier notifier = find(channel).orElseThrow(() -> new ValidationException(
                "el canal " + channel + " no existe o no está configurado"));
        notifier.validate(address);
    }

    private void register(Notifier notifier) {
        Notifier previous = notifiersByChannel.putIfAbsent(notifier.channel(), notifier);
        if (previous != null) {
            throw new IllegalStateException("Hay dos notificadores para el canal " + notifier.channel());
        }
    }
}
