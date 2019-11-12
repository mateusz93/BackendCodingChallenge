package com.collibra.interview.server;

import com.collibra.interview.core.MessageProcessor;
import com.collibra.interview.core.exception.UnsupportedCommandException;
import com.collibra.interview.graph.DirectedGraph;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Slf4j
public class SocketServer implements Runnable {

    private static final String CLIENT_PREFIX = "[CLIENT] ";
    private static final String SERVER_PREFIX = "[SERVER] ";
    private final MessageProcessor messageProcessor;
    private final Socket socket;
    private final int timeoutInMs;
    private PrintWriter out;

    @SneakyThrows
    SocketServer(final Socket socket, final int timeoutInMs) {
        this.socket = socket;
        this.socket.setSoTimeout(timeoutInMs);
        this.timeoutInMs = timeoutInMs;
        this.messageProcessor = new MessageProcessor(DirectedGraph.getInstance());
    }

    @SneakyThrows
    public void run() {
        log.debug("Starting new single client socket server");
        messageProcessor.resetTimer();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            sendWelcomeMessage();
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                sendAnswer(clientMessage);
            }
        } catch (SocketTimeoutException e) {
            sendTimeoutMessage();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void sendAnswer(String clientMessage) {
        log.info(CLIENT_PREFIX + clientMessage);
        try {
            final String serverAnswer = messageProcessor.process(clientMessage);
            log.info(SERVER_PREFIX + serverAnswer);
            out.println(serverAnswer);
        } catch (UnsupportedCommandException e) {
            log.info(SERVER_PREFIX + e.getMessage());
            out.println(e.getMessage());
        }
    }

    private void sendWelcomeMessage() {
        log.info(SERVER_PREFIX + messageProcessor.getWelcomeMessage());
        out.println(messageProcessor.getWelcomeMessage());
    }

    private void sendTimeoutMessage() {
        log.info(SERVER_PREFIX + messageProcessor.getTimeoutMessage(timeoutInMs));
        out.println(messageProcessor.getTimeoutMessage(timeoutInMs));
    }

}
