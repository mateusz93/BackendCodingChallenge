package com.collibra.interview.exception;

public class NodeNotFoundException extends MessageProcessingException {

    private static final String DEFAULT_MESSAGE = "ERROR: NODE NOT FOUND";

    public NodeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

}
