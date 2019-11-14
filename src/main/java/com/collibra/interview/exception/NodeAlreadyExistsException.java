package com.collibra.interview.exception;

public class NodeAlreadyExistsException extends MessageProcessingException {

    private static final String DEFAULT_MESSAGE = "ERROR: NODE ALREADY EXISTS";

    public NodeAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

}
