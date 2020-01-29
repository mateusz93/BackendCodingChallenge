package com.collibra.backend.challenge.core.node;

import com.collibra.backend.challenge.core.MessageProcessingException;

public class NodeAlreadyExistsException extends MessageProcessingException {

    private static final String DEFAULT_MESSAGE = "ERROR: NODE ALREADY EXISTS";

    public NodeAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

}
