package com.collibra.interview.core;

import com.collibra.interview.graph.DirectedGraph;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseMessageProcessor {

    protected static final String NODE_NOT_FOUND_MESSAGE = "ERROR: NODE NOT FOUND";
    protected final DirectedGraph graph;

}
