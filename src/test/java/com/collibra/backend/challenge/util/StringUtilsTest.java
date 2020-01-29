package com.collibra.backend.challenge.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringUtilsTest {
    
    @Test
    void shouldReturnPhraseFromIndicatedPosition() {
        final String text = "Some text for tests";
        assertEquals("Some", StringUtils.phraseAtPosition(text, 1));
        assertEquals("text", StringUtils.phraseAtPosition(text, 2));
        assertEquals("tests", StringUtils.phraseAtPosition(text, 4));
    }

    @Test
    void shouldThrowExceptionWhenPositionIsOutOfBoundary() {
        final String text = "Some text";
        assertThrows(IllegalArgumentException.class, () -> StringUtils.phraseAtPosition(text, 0), "0 is incorrect value. Position must be positive");
        assertThrows(IllegalArgumentException.class, () -> StringUtils.phraseAtPosition(text, -1), "-1 is incorrect value. Position must be positive");
        assertThrows(IllegalArgumentException.class, () -> StringUtils.phraseAtPosition(text, 0), "Incorrect position. Max value for passed input: 2");
    }
}