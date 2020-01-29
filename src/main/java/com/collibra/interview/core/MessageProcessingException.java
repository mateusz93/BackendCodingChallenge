package com.collibra.interview.core;

public abstract class MessageProcessingException extends RuntimeException {

    public MessageProcessingException(final String message) {
        super(message);
    }
}
