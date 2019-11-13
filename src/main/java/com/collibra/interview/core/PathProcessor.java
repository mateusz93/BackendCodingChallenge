package com.collibra.interview.core;

import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.graph.Node;
import com.collibra.interview.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PathProcessor extends BaseMessageProcessor {

    PathProcessor(final DirectedGraph graph) {
        super(graph);
    }

    String calculateShortestPath(final String message) {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        final int shortestPath = graph.findTheShortestPath(new Node(source), new Node(target));
        if (shortestPath == -1) {
            return NODE_NOT_FOUND_MESSAGE;
        }
        return String.valueOf(shortestPath);
    }

}
