package com.collibra.interview.server;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class MultiClientWebSocketServer {

    private static final int TIMEOUT_IN_MS = 30_000;
    private final int port;

    @SneakyThrows
    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (final Socket socket = serverSocket.accept()) {
                    log.debug("Starting new socket on port: " + port);
                    new WebSocketServer(socket,TIMEOUT_IN_MS).run();
                }
            }
        }
    }

}
