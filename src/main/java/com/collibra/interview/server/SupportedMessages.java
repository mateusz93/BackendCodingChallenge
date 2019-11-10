package com.collibra.interview.server;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SupportedMessages {

    private static final HashMap<String, String> CLIENT_SERVER_MESSAGES_MAPPING = HashMap.of(
            "^HI, I AM [-a-zA-Z0-9]*$", "HI <NAME>",
            "BYE MATE!", "BYE <NAME>, WE SPOKE FOR <X> MS");

    public static boolean isSupported(final String message) {
        return CLIENT_SERVER_MESSAGES_MAPPING.keySet()
                                             .filter(it -> Pattern.matches(it, message))
                                             .nonEmpty();
    }

}
