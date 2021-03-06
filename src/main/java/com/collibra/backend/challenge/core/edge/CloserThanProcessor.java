package com.collibra.backend.challenge.core.edge;

import com.collibra.backend.challenge.core.BaseMessageProcessor;
import com.collibra.backend.challenge.core.node.NodeNotFoundException;
import com.collibra.backend.challenge.graph.DirectedGraph;
import com.collibra.backend.challenge.graph.Node;
import com.collibra.backend.challenge.util.StringUtils;
import io.vavr.collection.List;

public final class CloserThanProcessor extends BaseMessageProcessor {

    public CloserThanProcessor(final DirectedGraph graph) {
        super(graph);
    }

    @Override
    public boolean isApplicable(final String message) {
        return message.startsWith("CLOSER THAN");
    }

    @Override
    public String process(final String message) {
        final int weight = Integer.parseInt(StringUtils.phraseAtPosition(message, 3));
        final String nodeName = StringUtils.phraseAtPosition(message, 4);
        try {
            final List<Node> nodes = graph.findAllCloserNodesThan(new Node(nodeName), weight);
            if (nodes.isEmpty()) {
                return "";
            }
            return nodes.map(Node::getName)
                    .sorted()
                    .reduce((a, b) -> a + "," + b)
                    .trim();
        } catch (IllegalArgumentException e) {
            throw new NodeNotFoundException();
        }
    }
}
