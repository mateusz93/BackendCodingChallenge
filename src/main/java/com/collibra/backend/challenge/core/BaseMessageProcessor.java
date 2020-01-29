package com.collibra.backend.challenge.core;

import com.collibra.backend.challenge.graph.DirectedGraph;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseMessageProcessor implements MessageProcessor {

    protected final DirectedGraph graph;

}
