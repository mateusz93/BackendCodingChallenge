package com.collibra.interview.core.edge;

import com.collibra.interview.core.BaseMessageProcessor;
import com.collibra.interview.core.node.NodeNotFoundException;
import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.graph.Node;
import com.collibra.interview.util.StringUtils;

public final class DeleteEdgeProcessor extends BaseMessageProcessor {

    public DeleteEdgeProcessor(final DirectedGraph graph) {
        super(graph);
    }

    @Override
    public boolean isApplicable(final String message) {
        return message.startsWith("REMOVE EDGE");
    }

    @Override
    public String process(final String message) {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        if (graph.removeEdge(new Node(source), new Node(target))) {
            return "EDGE REMOVED";
        } else {
            throw new NodeNotFoundException();
        }
    }
}
