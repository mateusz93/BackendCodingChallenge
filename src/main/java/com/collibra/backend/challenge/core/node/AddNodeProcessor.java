package com.collibra.backend.challenge.core.node;

import com.collibra.backend.challenge.core.BaseMessageProcessor;
import com.collibra.backend.challenge.graph.DirectedGraph;
import com.collibra.backend.challenge.graph.Node;
import com.collibra.backend.challenge.util.StringUtils;

public final class AddNodeProcessor extends BaseMessageProcessor {

    public AddNodeProcessor(final DirectedGraph graph) {
        super(graph);
    }

    @Override
    public boolean isApplicable(final String message) {
        return message.startsWith("ADD NODE");
    }

    @Override
    public String process(final String message) {
        final String nodeName = StringUtils.phraseAtPosition(message, 3);
        final Node node = new Node(nodeName);
        if (graph.addNode(node)) {
            return "NODE ADDED";
        } else {
            throw new NodeAlreadyExistsException();
        }
    }
}
