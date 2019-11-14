package com.collibra.interview.core;

import com.collibra.interview.exception.NodeNotFoundException;
import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.graph.Edge;
import com.collibra.interview.graph.Node;
import com.collibra.interview.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class EdgeProcessor extends BaseMessageProcessor {

    EdgeProcessor(final DirectedGraph graph) {
        super(graph);
    }

    String addEdge(final String message) throws NodeNotFoundException {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        final int weight = Integer.parseInt(StringUtils.phraseAtPosition(message, 5));
        final Edge edge = Edge.builder()
                              .source(new Node(source))
                              .target(new Node(target))
                              .weight(weight)
                              .build();
        if (graph.addEdge(edge)) {
            return "EDGE ADDED";
        }
        throw new NodeNotFoundException();
    }

    String removeEdge(final String message) throws NodeNotFoundException {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        if (graph.removeEdge(new Node(source), new Node(target))) {
            return "EDGE REMOVED";
        }
        throw new NodeNotFoundException();
    }
}
