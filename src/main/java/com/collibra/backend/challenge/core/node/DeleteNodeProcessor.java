package com.collibra.backend.challenge.core.node;

import com.collibra.backend.challenge.core.BaseMessageProcessor;
import com.collibra.backend.challenge.graph.DirectedGraph;
import com.collibra.backend.challenge.graph.Node;
import com.collibra.backend.challenge.util.StringUtils;

public final class DeleteNodeProcessor extends BaseMessageProcessor {

    public DeleteNodeProcessor(final DirectedGraph graph) {
        super(graph);
    }

    @Override
    public boolean isApplicable(final String message) {
        return message.startsWith("REMOVE NODE");
    }

    @Override
    public String process(final String message) {
        final String nodeName = StringUtils.phraseAtPosition(message, 3);
        final Node node = new Node(nodeName);
        if (graph.removeNode(node)) {
            return "NODE REMOVED";
        } else {
            throw new NodeNotFoundException();
        }
    }
}
