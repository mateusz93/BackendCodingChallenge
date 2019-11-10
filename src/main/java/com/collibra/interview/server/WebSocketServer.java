package com.collibra.interview.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
public class WebSocketServer implements Runnable {

    private static final String UNSUPPORTED_COMMAND = "SORRY, I DID NOT UNDERSTAND THAT";
    private static final String CLIENT_PREFIX = "[CLIENT] ";
    private static final String SERVER_PREFIX = "[SERVER] ";
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
            sendGreetingsMessage();
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                log.info(CLIENT_PREFIX + clientMessage);
                if (SupportedMessages.isSupported(clientMessage)) {
                    if (clientMessage.startsWith("HI")) {
                        clientName = getClientName(clientMessage);
                        log.info(SERVER_PREFIX + "HI " + clientName);
                        out.println("HI " + clientName);
                    }
                    if (clientMessage.startsWith("BYE")) {
                        Instant end = Instant.now();
                        log.info(SERVER_PREFIX + "BYE " + clientName + ", WE SPOKE FOR " + Duration.between(start, end).toMillis() + " MS");
                        out.println("BYE " + clientName + ", WE SPOKE FOR " + Duration.between(start, end).toMillis() + " MS");
                        break;
                    }
                } else {
                    sendUnsupportedCommandMessage();
                }
            }
        } catch (SocketTimeoutException e) {
            sendTimeoutMessage();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void sendGreetingsMessage() {
        final String greetings = "HI, I AM " + generateSessionId();
        log.info(SERVER_PREFIX + greetings);
        out.println(greetings);
    }

    private void sendUnsupportedCommandMessage() {
        log.info(SERVER_PREFIX + UNSUPPORTED_COMMAND);
        out.println(UNSUPPORTED_COMMAND);
    }

    private void sendTimeoutMessage() {
        final String timeoutMessage = "BYE " + clientName + ", WE SPOKE FOR " + TIMEOUT_IN_MS + " MS";
        log.info(SERVER_PREFIX + timeoutMessage);
        out.println(timeoutMessage);
    }

    private String getClientName(final String message) {
        return StringUtils.substringAfterLast(message, " ");
    }

    private UUID generateSessionId() {
        return UUID.randomUUID();
    }
}
