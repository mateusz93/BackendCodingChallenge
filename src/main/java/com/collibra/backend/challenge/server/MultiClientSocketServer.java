package com.collibra.backend.challenge.server;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class MultiClientSocketServer {

    private static final int TIMEOUT_IN_MS = 30_000;
    private final int port;

    @SneakyThrows(IOException.class)
    public void start() {
        log.info("Multi client server socket stared and listening on port {}", port);
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new SocketServer(socket, TIMEOUT_IN_MS));
            }
        }
    }

}
