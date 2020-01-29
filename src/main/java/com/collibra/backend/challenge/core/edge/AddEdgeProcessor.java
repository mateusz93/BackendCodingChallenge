package com.collibra.backend.challenge.core.edge;

import com.collibra.backend.challenge.core.BaseMessageProcessor;
import com.collibra.backend.challenge.core.node.NodeNotFoundException;
import com.collibra.backend.challenge.core.UnsupportedCommandException;
import com.collibra.backend.challenge.graph.DirectedGraph;
import com.collibra.backend.challenge.graph.Edge;
import com.collibra.backend.challenge.graph.Node;
import com.collibra.backend.challenge.util.StringUtils;

public final class AddEdgeProcessor extends BaseMessageProcessor {

    public AddEdgeProcessor(final DirectedGraph graph) {
        super(graph);
    }

    @Override
    public boolean isApplicable(final String message) {
        return message.startsWith("ADD EDGE");
    }

    @Override
    public String process(final String message) {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        final int weight = Integer.parseInt(StringUtils.phraseAtPosition(message, 5));
        final Edge edge = buildEdge(source, target, weight);
        if (graph.addEdge(edge)) {
            return "EDGE ADDED";
        } else {
            throw new NodeNotFoundException();
        }
    }

    private Edge buildEdge(String source, String target, int weight) {
        if (weight < 0) {
            throw new UnsupportedCommandException();
        }
        return Edge.builder()
                .source(new Node(source))
                .target(new Node(target))
                .weight(weight)
                .build();
    }
}
