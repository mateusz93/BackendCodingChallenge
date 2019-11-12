package com.collibra.interview.server;

import com.collibra.interview.core.MessageResolver;
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
public class WebSocketServer implements Runnable {

    private static final String CLIENT_PREFIX = "[CLIENT] ";
    private static final String SERVER_PREFIX = "[SERVER] ";
    private final MessageResolver messageResolver;
    private final Socket socket;
    private final int timeoutInMs;
    private PrintWriter out;

    @SneakyThrows
    WebSocketServer(final Socket socket, final int timeoutInMs) {
        this.socket = socket;
        this.socket.setSoTimeout(timeoutInMs);
        this.timeoutInMs = timeoutInMs;
        this.messageResolver = new MessageResolver(DirectedGraph.getInstance());
    }

    @SneakyThrows
    public void run() {
        messageResolver.resetTimer();
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
            final String serverAnswer = messageResolver.resolve(clientMessage);
            log.info(SERVER_PREFIX + serverAnswer);
            out.println(serverAnswer);
        } catch (UnsupportedCommandException e) {
            log.info(SERVER_PREFIX + e.getMessage());
            out.println(e.getMessage());
        }
    }

    private void sendWelcomeMessage() {
        log.info(SERVER_PREFIX + messageResolver.getWelcomeMessage());
        out.println(messageResolver.getWelcomeMessage());
    }

    private void sendTimeoutMessage() {
        log.info(SERVER_PREFIX + messageResolver.getTimeoutMessage(timeoutInMs));
        out.println(messageResolver.getTimeoutMessage(timeoutInMs));
    }

}
