package com.collibra.interview.core.node;

import com.collibra.interview.core.MessageProcessingException;

public class NodeAlreadyExistsException extends MessageProcessingException {

    private static final String DEFAULT_MESSAGE = "ERROR: NODE ALREADY EXISTS";

    public NodeAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

}
