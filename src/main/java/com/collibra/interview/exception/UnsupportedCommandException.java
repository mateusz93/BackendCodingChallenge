package com.collibra.interview.exception;

public class UnsupportedCommandException extends Exception {

    private static final String DEFAULT_MESSAGE = "SORRY, I DID NOT UNDERSTAND THAT";

    public UnsupportedCommandException() {
        super(DEFAULT_MESSAGE);
    }

    public UnsupportedCommandException(final String message) {
        super(message);
    }
}
