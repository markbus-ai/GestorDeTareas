package com.trullo.core;

import com.trullo.exception.NotFoundException;
import com.trullo.exception.NotificationException;
import com.trullo.exception.ProyectoException;
import com.trullo.exception.ServiceException;
import com.trullo.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/** Registra el detalle técnico y obtiene los mensajes de aplicación para la interfaz. */
public final class ErrorReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorReporter.class);

    private ErrorReporter() {
    }

    /** SLF4J imprime también causas y excepciones suprimidas. */
    public static String report(String context, Throwable error) {
        LOGGER.error(context, error);
        return message(error, context);
    }

    public static String message(Throwable error, String fallback) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Set<String> messages = new LinkedHashSet<>();
        for (Throwable cause = error; cause != null && visited.add(cause); cause = cause.getCause()) {
            if (cause instanceof ValidationException || cause instanceof NotFoundException
                    || cause instanceof ServiceException || cause instanceof ProyectoException
                    || cause instanceof NotificationException) {
                if (cause.getMessage() != null && !cause.getMessage().isBlank()) {
                    messages.add(cause.getMessage());
                }
            }
        }
        return messages.isEmpty() ? fallback : String.join("\n", messages);
    }
}
