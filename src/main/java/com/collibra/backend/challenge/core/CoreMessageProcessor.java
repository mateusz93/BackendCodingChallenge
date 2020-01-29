package com.collibra.backend.challenge.core;

import com.collibra.backend.challenge.core.edge.CloserThanProcessor;
import com.collibra.backend.challenge.core.edge.DeleteEdgeProcessor;
import com.collibra.backend.challenge.core.edge.ShortestPathProcessor;
import com.collibra.backend.challenge.core.io.GreetingsProcessor;
import com.collibra.backend.challenge.core.node.AddNodeProcessor;
import com.collibra.backend.challenge.core.node.DeleteNodeProcessor;
import com.collibra.backend.challenge.core.edge.AddEdgeProcessor;
import com.collibra.backend.challenge.graph.DirectedGraph;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class CoreMessageProcessor {

    private final List<MessageProcessor> processors;
    private final UUID sessionId;
    private final Instant timer;
    private final int TIMEOUT_IN_MS = 30_000;

    public CoreMessageProcessor(final DirectedGraph graph, final UUID sessionId, final Instant timer) {
        this.sessionId = sessionId;
        this.timer = timer;
        this.processors = initProcessors(graph);
    }

    private List<MessageProcessor> initProcessors(DirectedGraph graph) {
        return List.of(
                new AddEdgeProcessor(graph),
                new DeleteEdgeProcessor(graph),
                new AddNodeProcessor(graph),
                new DeleteNodeProcessor(graph),
                new CloserThanProcessor(graph),
                new ShortestPathProcessor(graph),
                new GreetingsProcessor(graph)
        );
    }

    public String process(final String message) {
        if (isLastMessage(message)) {
            return String.format("BYE %s, WE SPOKE FOR %d MS", getClientName(), calculateConnectionTime());
        }
        return processors.filter(processor -> processor.isApplicable(message))
                .map(processor -> processor.process(message))
                .getOrElseThrow(UnsupportedCommandException::new);
    }

    private long calculateConnectionTime() {
        return Duration.between(timer, Instant.now()).toMillis();
    }

    private boolean isLastMessage(final String message) {
        return "BYE MATE!".equals(message);
    }

    public String getWelcomeMessage() {
        return "HI, I AM " + sessionId.toString();
    }

    public String getTimeoutMessage() {
        String clientName = getClientName();
        return "BYE " + clientName + ", WE SPOKE FOR " + TIMEOUT_IN_MS + " MS";
    }

    public String getClientName() {
        Option<String> clientName = processors.map(MessageProcessor::getClientName)
                .filter(Option::isDefined)
                .getOrElse(Option::none);
        if (clientName.isEmpty()) {
            throw new IllegalStateException("Can not generate timeout message without client name");
        }
        return clientName.get();
    }

}
