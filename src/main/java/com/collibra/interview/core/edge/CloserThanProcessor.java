package com.collibra.interview.core.edge;

import com.collibra.interview.core.BaseMessageProcessor;
import com.collibra.interview.core.node.NodeNotFoundException;
import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.graph.Node;
import com.collibra.interview.util.StringUtils;
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
