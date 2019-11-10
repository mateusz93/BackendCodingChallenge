package com.collibra.interview.server;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class WebSocketServer implements Runnable {

    private static final String UNSUPPORTED_COMMAND = "SORRY, I DID NOT UNDERSTAND THAT";
    private static final int TIMEOUT_IN_MS = 30 * 1_000;
    private final Socket socket;
    private PrintWriter out;
    private String clientName;

    @SneakyThrows
    WebSocketServer(Socket socket) {
        this.socket = socket;
        this.socket.setSoTimeout(TIMEOUT_IN_MS);
    }

    @SneakyThrows
    public void run() {
        Instant start = Instant.now();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HI, I AM " + generateSessionId());
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                if (SupportedMessages.isSupported(clientMessage)) {
                    if (clientMessage.startsWith("HI")) {
                        clientName = getClientName(clientMessage);
                        out.println("HI " + clientName);
                    }
                    if (clientMessage.startsWith("BYE")) {
                        Instant end = Instant.now();
                        out.println("BYE " + clientName + ", WE SPOKE FOR " + Duration.between(start, end).toMillis() + " MS");
                        break;
                    }
                } else {
                    out.println(UNSUPPORTED_COMMAND);
                }
            }
        } catch (SocketTimeoutException e) {
            out.println("BYE " + clientName + ", WE SPOKE FOR " + TIMEOUT_IN_MS + " MS");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private String getClientName(final String message) {
        return StringUtils.substringAfterLast(message, "\\s+");
    }

    private UUID generateSessionId() {
        return UUID.randomUUID();
    }
}
