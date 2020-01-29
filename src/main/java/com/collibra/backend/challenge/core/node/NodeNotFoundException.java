package com.collibra.backend.challenge.core.node;

import com.collibra.backend.challenge.core.MessageProcessingException;

public class NodeNotFoundException extends MessageProcessingException {

    private static final String DEFAULT_MESSAGE = "ERROR: NODE NOT FOUND";

    public NodeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

}
