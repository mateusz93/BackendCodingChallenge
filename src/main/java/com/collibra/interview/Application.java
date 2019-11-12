package com.collibra.interview;

import com.collibra.interview.server.MultiClientSocketServer;

public class Application {

    public static void main(String[] args) {
        final MultiClientSocketServer socketServer = new MultiClientSocketServer(50_000);
        socketServer.start();
    }
}
