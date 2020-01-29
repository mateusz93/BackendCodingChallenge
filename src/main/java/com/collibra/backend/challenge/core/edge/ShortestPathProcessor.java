package com.collibra.backend.challenge.core.edge;

import com.collibra.backend.challenge.core.BaseMessageProcessor;
import com.collibra.backend.challenge.core.node.NodeNotFoundException;
import com.collibra.backend.challenge.graph.DirectedGraph;
import com.collibra.backend.challenge.graph.Node;
import com.collibra.backend.challenge.util.StringUtils;

public final class ShortestPathProcessor extends BaseMessageProcessor {

    public ShortestPathProcessor(final DirectedGraph graph) {
        super(graph);
    }

    @Override
    public boolean isApplicable(final String message) {
        return message.startsWith("SHORTEST PATH");
    }

    @Override
    public String process(final String message) {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        final int shortestPath = graph.findTheShortestPath(new Node(source), new Node(target));
        if (shortestPath == -1) {
            throw new NodeNotFoundException();
        }
        return String.valueOf(shortestPath);
    }

}
