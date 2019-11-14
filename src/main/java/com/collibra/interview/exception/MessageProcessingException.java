package com.collibra.interview.exception;

public abstract class MessageProcessingException extends Exception {

    MessageProcessingException(final String message) {
        super(message);
    }
}
