package com.collibra.interview.core;

import com.collibra.interview.graph.DirectedGraph;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseMessageProcessor implements MessageProcessor {

    protected final DirectedGraph graph;

}
