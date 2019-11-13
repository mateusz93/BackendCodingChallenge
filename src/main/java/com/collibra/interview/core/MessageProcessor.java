package com.collibra.interview.core;

import com.collibra.interview.exception.UnsupportedCommandException;
import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.graph.Edge;
import com.collibra.interview.graph.Node;
import com.collibra.interview.util.StringUtils;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class MessageProcessor {

    private static final String NODE_NOT_FOUND_MESSAGE = "ERROR: NODE NOT FOUND";
    private static final String ALPHANUMERIC_DASH_REGEXP = "[-a-zA-Z0-9]*";
    private final DirectedGraph graph;
    @Getter
    private final UUID sessionId;
    @Getter
    private final Instant timer;
    @Getter
    private String clientName;

    private static final List<String> SUPPORTED_CLIENT_MESSAGES_PATTERNS = List.of(
            "^HI, I AM " + ALPHANUMERIC_DASH_REGEXP + "$",
            "BYE MATE!",
            "^ADD NODE " + ALPHANUMERIC_DASH_REGEXP + "$",
            "^ADD EDGE " + ALPHANUMERIC_DASH_REGEXP + " " + ALPHANUMERIC_DASH_REGEXP + " \\d+$",
            "^REMOVE NODE " + ALPHANUMERIC_DASH_REGEXP + "$",
            "^REMOVE EDGE " + ALPHANUMERIC_DASH_REGEXP + " " + ALPHANUMERIC_DASH_REGEXP + "$",
            "^SHORTEST PATH " + ALPHANUMERIC_DASH_REGEXP + " " + ALPHANUMERIC_DASH_REGEXP + "$",
            "^CLOSER THAN " + "\\d+ " + ALPHANUMERIC_DASH_REGEXP + "$");

    public MessageProcessor(final DirectedGraph graph) {
        this.graph = graph;
        this.sessionId = UUID.randomUUID();
        this.timer = Instant.now();
    }

    public String getWelcomeMessage() {
        return "HI, I AM " + sessionId;
    }

    public String getTimeoutMessage(int ms) {
        if (clientName == null) {
            throw new IllegalStateException("Can not generate timeout message without client name");
        }
        return "BYE " + clientName + ", WE SPOKE FOR " + ms + " MS";
    }

    public String process(final String message) throws UnsupportedCommandException {
        if (isNotSupported(message)) {
            throw new UnsupportedCommandException();
        }
        if (message.startsWith("HI, I AM")) {
            return generateGreetingsMessage(message);
        } else if (message.equals("BYE MATE!")) {
            return generateGoodbyeMessage();
        } else if (message.startsWith("ADD NODE")) {
            return addNode(message);
        } else if (message.startsWith("ADD EDGE")) {
            return addEdge(message);
        } else if (message.startsWith("REMOVE NODE")) {
            return removeNode(message);
        } else if (message.startsWith("REMOVE EDGE")) {
            return removeEdge(message);
        } else if (message.startsWith("SHORTEST PATH")) {
            return calculateShortestPath(message);
        } else if (message.startsWith("CLOSER THAN")) {
            return findAllCloserNodes(message);
        }
        throw new IllegalArgumentException("Can not resolve message: " + message);
    }

    boolean isNotSupported(final String message) {
        return SUPPORTED_CLIENT_MESSAGES_PATTERNS.filter(regex -> Pattern.matches(regex, message))
                                                 .isEmpty();
    }

    private String generateGoodbyeMessage() {
        if (clientName == null) {
            throw new IllegalStateException("Can not generate timeout message without client name");
        }
        return "BYE " + clientName + ", WE SPOKE FOR " + Duration.between(timer, Instant.now()).toMillis() + " MS";
    }

    private String generateGreetingsMessage(final String message) {
        clientName = StringUtils.phraseAtPosition(message, 4);
        return "HI " + clientName;
    }

    private String addNode(final String message) {
        final String nodeName = StringUtils.phraseAtPosition(message, 3);
        final Node node = new Node(nodeName);
        if (graph.addNode(node)) {
            return "NODE ADDED";
        }
        return "ERROR: NODE ALREADY EXISTS";
    }

    private String addEdge(final String message) {
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
        return NODE_NOT_FOUND_MESSAGE;
    }

    private String removeNode(final String message) {
        final String nodeName = StringUtils.phraseAtPosition(message, 3);
        final Node node = new Node(nodeName);
        if (graph.removeNode(node)) {
            return "NODE REMOVED";
        }
        return NODE_NOT_FOUND_MESSAGE;
    }

    private String removeEdge(final String message) {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        if (graph.removeEdge(new Node(source), new Node(target))) {
            return "EDGE REMOVED";
        }
        return NODE_NOT_FOUND_MESSAGE;
    }

    private String calculateShortestPath(final String message) {
        final String source = StringUtils.phraseAtPosition(message, 3);
        final String target = StringUtils.phraseAtPosition(message, 4);
        final int shortestPath = graph.findTheShortestPath(new Node(source), new Node(target));
        if (shortestPath == -1) {
            return NODE_NOT_FOUND_MESSAGE;
        }
        return String.valueOf(shortestPath);
    }

    private String findAllCloserNodes(final String message) {
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
