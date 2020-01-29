package com.collibra.backend.challenge.core;

import io.vavr.control.Option;

public interface MessageProcessor {

    boolean isApplicable(final String message);

    String process(final String message);

    default Option<String> getClientName() {
        return Option.none();
    }

}
