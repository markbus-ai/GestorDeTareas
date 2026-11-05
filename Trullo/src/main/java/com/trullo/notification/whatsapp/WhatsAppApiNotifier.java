package com.trullo.notification.whatsapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trullo.exception.NotificationException;
import com.trullo.exception.ValidationException;
import com.trullo.interfaces.notification.ScheduledNotifier;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Programa WhatsApp en Truello-Api. No envía mensajes ni abre sesiones localmente. */
public final class WhatsAppApiNotifier implements ScheduledNotifier {
    public static final String CHANNEL = "whatsapp";
    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppApiNotifier.class);
    private final URI endpoint;
    private final String apiKey;
    private final ZoneId serverZone;
    private final HttpClient http;
    private final Gson gson = new Gson();

    public WhatsAppApiNotifier(String baseUrl, String apiKey, ZoneId serverZone) {
        URI base = URI.create(baseUrl);
        if (!("http".equals(base.getScheme()) || "https".equals(base.getScheme()))
                || base.getHost() == null || base.getQuery() != null || base.getFragment() != null
                || base.getUserInfo() != null || apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Configuracion de Truello-Api invalida");
        }
        this.endpoint = URI.create(baseUrl.replaceAll("/+$", "") + "/wsp-queues");
        this.apiKey = apiKey;
        this.serverZone = serverZone;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER).build();
    }

    @Override
    public String channel() {
        return CHANNEL;
    }

    @Override
    public void validate(String address) {
        if (address == null || !address.matches("\\+?[1-9][0-9]{6,14}")) {
            throw new ValidationException("El telefono debe tener entre 7 y 15 digitos, con + opcional");
        }
    }

    @Override
    public void schedule(String address, String taskTitle, LocalDateTime scheduledAt) {
        validate(address);
        if (taskTitle == null || taskTitle.isBlank() || taskTitle.codePointCount(0, taskTitle.length()) > 500) {
            throw new ValidationException("La tarea debe tener entre 1 y 500 caracteres");
        }
        if (scheduledAt == null) {
            throw new ValidationException("La fecha del recordatorio es obligatoria");
        }
        var time = scheduledAt.atZone(ZoneId.systemDefault()).withZoneSameInstant(serverZone)
                .withSecond(0).withNano(0);
        if (!time.toInstant().isAfter(java.time.Instant.now())) {
            throw new ValidationException("El recordatorio debe programarse en un minuto futuro");
        }
        String body = gson.toJson(Map.of("phone", address, "tarea", taskTitle,
                "fecha", time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "hora", time.format(DateTimeFormatter.ofPattern("HH:mm"))));
        HttpRequest request = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(15))
                .header("X-API-Key", apiKey).header("Content-Type", "application/json")
                .header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                if (response.statusCode() == 401 || response.statusCode() == 403) {
                    throw new NotificationException("Truello-Api rechazo la clave de acceso");
                }
                if (response.statusCode() == 400 || response.statusCode() == 422) {
                    throw new NotificationException("Truello-Api rechazo el recordatorio: "
                            + detail(response.body()));
                }
                throw new NotificationException("Truello-Api respondio HTTP " + response.statusCode()
                        + ". No se reintento para evitar duplicados");
            }
            JsonObject result = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!result.has("id") || result.get("id").getAsLong() <= 0
                    || !result.has("state") || !"pending".equals(result.get("state").getAsString())) {
                throw new IllegalStateException("Respuesta sin confirmacion de programacion");
            }
            LOGGER.info("WhatsApp programado en la API; cola {}", result.get("id").getAsLong());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotificationException("Se interrumpio la programacion. Consulta la cola antes de reintentar", e);
        } catch (IOException e) {
            throw new NotificationException("No se pudo confirmar la programacion en Truello-Api. "
                    + "Consulta la cola antes de reintentar", e);
        } catch (NotificationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new NotificationException("Respuesta inesperada de Truello-Api. "
                    + "Consulta la cola antes de reintentar", e);
        }
    }

    private String detail(String body) {
        try {
            String value = JsonParser.parseString(body).getAsJsonObject().get("detail").getAsString();
            return value.length() <= 500 ? value : value.substring(0, 500);
        } catch (RuntimeException e) {
            return "verifica telefono, tarea, fecha y hora";
        }
    }
}
