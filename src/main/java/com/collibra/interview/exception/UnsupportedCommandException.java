package com.collibra.interview.exception;

public class UnsupportedCommandException extends MessageProcessingException {

    private static final String DEFAULT_MESSAGE = "SORRY, I DID NOT UNDERSTAND THAT";

    public UnsupportedCommandException() {
        super(DEFAULT_MESSAGE);
    }

}
