package com.collibra.interview;

import com.collibra.interview.server.MultiClientWebSocketServer;

public class Application {

    public static void main(String[] args) {
        final MultiClientWebSocketServer socketServer = new MultiClientWebSocketServer(50_000);
        socketServer.start();
    }
}
