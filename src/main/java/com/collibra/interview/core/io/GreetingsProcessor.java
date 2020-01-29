package com.collibra.interview.core.io;

import com.collibra.interview.core.BaseMessageProcessor;
import com.collibra.interview.graph.DirectedGraph;
import com.collibra.interview.util.StringUtils;
import io.vavr.control.Option;

public final class GreetingsProcessor extends BaseMessageProcessor {

    private Option<String> clientName = Option.none();

    public GreetingsProcessor(final DirectedGraph graph) {
        super(graph);
    }

    @Override
    public boolean isApplicable(final String message) {
        return message.startsWith("HI, I AM");
    }

    @Override
    public String process(final String message) {
        clientName = Option.of(StringUtils.phraseAtPosition(message, 4));
        return "HI " + clientName.get();
    }

    @Override
    public Option<String> getClientName() {
        return clientName;
    }
}
