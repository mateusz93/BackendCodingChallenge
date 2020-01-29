package com.collibra.interview.core.node;

import com.collibra.interview.core.MessageProcessingException;

public class NodeNotFoundException extends MessageProcessingException {

    private static final String DEFAULT_MESSAGE = "ERROR: NODE NOT FOUND";

    public NodeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

}
