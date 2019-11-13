package com.collibra.interview.core;

import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.graph.Node;
import com.collibra.interview.util.StringUtils;
import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class NodeProcessor extends BaseMessageProcessor {

    NodeProcessor(final DirectedGraph graph) {
        super(graph);
    }

    String addNode(final String message) {
        final String nodeName = StringUtils.phraseAtPosition(message, 3);
        final Node node = new Node(nodeName);
        if (graph.addNode(node)) {
            return "NODE ADDED";
        }
        return "ERROR: NODE ALREADY EXISTS";
    }

    String removeNode(final String message) {
        final String nodeName = StringUtils.phraseAtPosition(message, 3);
        final Node node = new Node(nodeName);
        if (graph.removeNode(node)) {
            return "NODE REMOVED";
        }
        return NODE_NOT_FOUND_MESSAGE;
    }

    String findAllCloserNodes(final String message) {
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
            return NODE_NOT_FOUND_MESSAGE;
        }
    }
}
