package com.collibra.interview.core;

import com.collibra.interview.exception.MessageProcessingException;
import com.collibra.interview.exception.UnsupportedCommandException;
import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.util.StringUtils;
import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Pattern;

@Slf4j
public class MessageProcessor {

    private static final String ALPHANUMERIC_DASH_REGEXP = "[-a-zA-Z0-9]*";
    private final EdgeProcessor edgeProcessor;
    private final NodeProcessor nodeProcessor;
    private final PathProcessor pathProcessor;
    private final Instant timer;
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

    public MessageProcessor(final DirectedGraph graph, final Instant timer) {
        this.edgeProcessor = new EdgeProcessor(graph);
        this.nodeProcessor = new NodeProcessor(graph);
        this.pathProcessor = new PathProcessor(graph);
        this.timer = timer;
    }

    public String getWelcomeMessage(final String name) {
        return "HI, I AM " + name;
    }

    public String getTimeoutMessage(final int ms) {
        if (clientName == null) {
            throw new IllegalStateException("Can not generate timeout message without client name");
        }
        return "BYE " + clientName + ", WE SPOKE FOR " + ms + " MS";
    }

    public String process(final String message) throws MessageProcessingException {
        if (isNotSupported(message)) {
            throw new UnsupportedCommandException();
        }
        if (message.startsWith("HI, I AM")) {
            return generateGreetingsMessage(message);
        } else if (message.equals("BYE MATE!")) {
            return generateGoodbyeMessage();
        } else if (message.startsWith("ADD NODE")) {
            return nodeProcessor.addNode(message);
        } else if (message.startsWith("ADD EDGE")) {
            return edgeProcessor.addEdge(message);
        } else if (message.startsWith("REMOVE NODE")) {
            return nodeProcessor.removeNode(message);
        } else if (message.startsWith("REMOVE EDGE")) {
            return edgeProcessor.removeEdge(message);
        } else if (message.startsWith("SHORTEST PATH")) {
            return pathProcessor.calculateShortestPath(message);
        } else if (message.startsWith("CLOSER THAN")) {
            return nodeProcessor.findAllCloserNodes(message);
        }
        throw new IllegalArgumentException("Can not resolve message: " + message);
    }

    boolean isNotSupported(final String message) {
        return SUPPORTED_CLIENT_MESSAGES_PATTERNS.filter(regex -> Pattern.matches(regex, message))
                                                 .isEmpty();
    }

    private String generateGreetingsMessage(final String message) {
        clientName = StringUtils.phraseAtPosition(message, 4);
        return "HI " + clientName;
    }

    private String generateGoodbyeMessage() {
        if (clientName == null) {
            throw new IllegalStateException("Can not generate timeout message without client name");
        }
        return "BYE " + clientName + ", WE SPOKE FOR " + Duration.between(timer, Instant.now()).toMillis() + " MS";
    }

}
