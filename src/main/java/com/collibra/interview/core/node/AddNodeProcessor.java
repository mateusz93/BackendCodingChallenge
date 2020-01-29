package com.collibra.interview.core.node;

import com.collibra.interview.core.BaseMessageProcessor;
import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.graph.Node;
import com.collibra.interview.util.StringUtils;

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
