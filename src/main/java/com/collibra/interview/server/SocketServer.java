package com.collibra.interview.server;

import com.collibra.interview.core.MessageProcessor;
import com.collibra.interview.exception.MessageProcessingException;
import com.collibra.interview.graph.DirectedGraph;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
public class SocketServer implements Runnable {

    private static final String CLIENT_PREFIX = "[CLIENT] ";
    private static final String SERVER_PREFIX = "[SERVER] ";
    private final MessageProcessor messageProcessor;
    private final Socket socket;
    private final UUID sessionId;
    private final int timeoutInMs;
    private PrintWriter out;

    @SneakyThrows
    SocketServer(final Socket socket, final int timeoutInMs) {
        this.socket = socket;
        this.socket.setSoTimeout(timeoutInMs);
        this.timeoutInMs = timeoutInMs;
        this.sessionId = UUID.randomUUID();
        this.messageProcessor = new MessageProcessor(DirectedGraph.getInstance(), Instant.now());
    }

    public void run() {
        log.info("Starting new single client socket server");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            sendWelcomeMessage();
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                sendAnswer(clientMessage);
            }
        } catch (SocketTimeoutException e) {
            sendTimeoutMessage();
        } catch (Exception e) {
            log.error("Unexpected exception occurred", e);
        } finally {
            if (out != null) {
                out.close();
            }
            log.info("Single client socket server closed");
        }
    }

    private void sendAnswer(final String clientMessage) {
        if (clientMessage.isEmpty()) {
            return;
        }
        log.debug(CLIENT_PREFIX + clientMessage);
        try {
            final String serverAnswer = messageProcessor.process(clientMessage);
            log.debug(SERVER_PREFIX + serverAnswer);
            out.println(serverAnswer);
        } catch (MessageProcessingException e) {
            log.debug(SERVER_PREFIX + e.getMessage());
            out.println(e.getMessage());
        }
    }

    private void sendWelcomeMessage() {
        log.debug(SERVER_PREFIX + messageProcessor.getWelcomeMessage(sessionId.toString()));
        out.println(messageProcessor.getWelcomeMessage(sessionId.toString()));
    }

    private void sendTimeoutMessage() {
        log.debug(SERVER_PREFIX + messageProcessor.getTimeoutMessage(timeoutInMs));
        out.println(messageProcessor.getTimeoutMessage(timeoutInMs));
    }

}
