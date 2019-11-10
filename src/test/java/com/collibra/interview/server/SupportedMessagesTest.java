package com.collibra.interview.server;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupportedMessagesTest {

    @ParameterizedTest
    @ValueSource(strings = {"HI, I AM some-name", "HI, I AM name", "HI, I AM 2e466994-4272-49b8-997d-b4aefd895982", "BYE MATE!"})
    void shouldVerifySupportedMessages(String message) {
        assertTrue(SupportedMessages.isSupported(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"HI, I AM", "HI, I AM NOT name", "BYE TOM!"})
    void shouldVerifyNotSupportedMessages(String message) {
        assertFalse(SupportedMessages.isSupported(message));
    }
}