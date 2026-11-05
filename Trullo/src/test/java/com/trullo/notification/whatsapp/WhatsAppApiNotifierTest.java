package com.trullo.notification.whatsapp;

import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import com.trullo.exception.NotificationException;
import com.trullo.exception.ValidationException;
import org.junit.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

public class WhatsAppApiNotifierTest {
    private HttpServer server;
    private final AtomicInteger calls = new AtomicInteger();
    private final AtomicReference<String> body = new AtomicReference<>();
    private final AtomicReference<String> key = new AtomicReference<>();
    private int status = 200;
    private String response = "{\"id\":42,\"state\":\"pending\"}";

    @Before public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/trullo/wsp-queues", exchange -> {
            calls.incrementAndGet();
            key.set(exchange.getRequestHeaders().getFirst("X-API-Key"));
            body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (var out = exchange.getResponseBody()) { out.write(bytes); }
        });
        server.start();
    }
    @After public void stop() { server.stop(0); }
    private WhatsAppApiNotifier client() {
        return new WhatsAppApiNotifier("http://127.0.0.1:" + server.getAddress().getPort() + "/trullo/",
                "test-key", ZoneId.of("America/Argentina/Buenos_Aires"));
    }
    @Test public void sendsPayloadHeadersAndServerTime() {
        var local = LocalDateTime.of(2099, 12, 15, 9, 30);
        client().schedule("+5491112345678", "Tarea \"especial\"\nñ", local);
        var json = JsonParser.parseString(body.get()).getAsJsonObject();
        var expected = local.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/Argentina/Buenos_Aires"));
        assertEquals("test-key", key.get());
        assertEquals("+5491112345678", json.get("phone").getAsString());
        assertEquals("Tarea \"especial\"\nñ", json.get("tarea").getAsString());
        assertEquals(expected.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), json.get("fecha").getAsString());
        assertEquals(expected.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), json.get("hora").getAsString());
        assertEquals(1, calls.get());
    }
    @Test public void handlesAuthenticationFailureWithoutRetry() {
        status = 401; response = "{\"detail\":\"unauthorized\"}";
        var error = assertThrows(NotificationException.class, () -> client().schedule("1234567", "Tarea", LocalDateTime.now().plusDays(1)));
        assertTrue(error.getMessage().contains("clave")); assertEquals(1, calls.get());
    }
    @Test public void propagatesValidationMessage() {
        status = 422; response = "{\"detail\":\"fecha invalida\"}";
        var error = assertThrows(NotificationException.class, () -> client().schedule("1234567", "Tarea", LocalDateTime.now().plusDays(1)));
        assertTrue(error.getMessage().contains("fecha invalida"));
    }
    @Test public void rejectsHtmlOrMissingConfirmation() {
        for (String invalid : new String[]{"<html>404</html>", "{}", "{\"id\":1,\"state\":\"sent\"}"}) {
            response = invalid;
            assertThrows(NotificationException.class, () -> client().schedule("1234567", "Tarea", LocalDateTime.now().plusDays(1)));
        }
    }
    @Test public void doesNotFollowRedirectOrRetryServerError() {
        for (int code : new int[]{301, 404, 500}) {
            status = code;
            assertThrows(NotificationException.class, () -> client().schedule("1234567", "Tarea", LocalDateTime.now().plusDays(1)));
        }
        assertEquals(3, calls.get());
    }
    @Test public void validatesBeforeNetwork() {
        assertThrows(ValidationException.class, () -> client().schedule("123", "Tarea", LocalDateTime.now().plusDays(1)));
        assertThrows(ValidationException.class, () -> client().schedule("1234567", "", LocalDateTime.now().plusDays(1)));
        assertThrows(ValidationException.class, () -> client().schedule("1234567", "Tarea", LocalDateTime.now().minusDays(1)));
        assertEquals(0, calls.get());
    }
}
