package com.collibra.interview.server;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.ServerSocket;
import java.net.Socket;

@RequiredArgsConstructor
public class MultiClientWebSocketServer {

    private final int port;

    @SneakyThrows
    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (final Socket socket = serverSocket.accept()) {
                    new WebSocketServer(socket).run();
                }
            }
        }
    }

}
