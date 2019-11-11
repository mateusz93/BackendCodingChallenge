package com.collibra.interview.core;

import com.collibra.interview.core.exception.UnsupportedCommandException;
import com.collibra.interview.graph.DirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageResolverTest {

    private MessageResolver resolver;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field instance = DirectedGraph.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        resolver = new MessageResolver(DirectedGraph.getInstance());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HI, I AM some-name", "HI, I AM name", "HI, I AM 2e466994-4272-49b8-997d-b4aefd895982", "BYE MATE!",
            "ADD NODE phase2-node1", "ADD EDGE phase2-node1 phase2-node2 17",
            "REMOVE NODE phase2-node1", "REMOVE EDGE phase2-node1 phase2-node2"})
    void shouldVerifySupportedMessages(String message) {
        assertFalse(resolver.isNotSupported(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"HI, I AM", "HI, I AM NOT name", "BYE TOM!",
            "ADD NODE", "ADD EDGE", "ADD EDGE phase2-node1 phase2-node2 -1",
            "REMOVE NODE", "REMOVE EDGE phase2-node1", "REMOVE EDGE phase2-node1 phase2-node2 2"})
    void shouldVerifyNotSupportedMessages(String message) {
        assertTrue(resolver.isNotSupported(message));
    }

    @Test
    void shouldThrowUnsupportedException() {
        assertThrows(UnsupportedCommandException.class, () -> resolver.resolve("UNKNOWN"), "SORRY, I DID NOT UNDERSTAND THAT");
    }

    @Test
    void shouldReturnGreetingsMessage() throws UnsupportedCommandException {
        assertEquals("HI John", resolver.resolve("HI, I AM John"));
    }

    @Test
    void shouldReturnGoodbyeMessage() throws UnsupportedCommandException {
        resolver.resolve("HI, I AM John");
        final String result = resolver.resolve("BYE MATE!");
        assertTrue(result.matches("^BYE John, WE SPOKE FOR \\d+ MS*$"));
    }

    @Test
    void shouldReturnNodeAddedMessage() throws UnsupportedCommandException {
        assertEquals("NODE ADDED", resolver.resolve("ADD NODE phase2-node1"));
    }

    @Test
    void shouldReturnNodeAlreadyExistsMessage() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase2-node1");
        assertEquals("ERROR: NODE ALREADY EXISTS", resolver.resolve("ADD NODE phase2-node1"));
    }

    @Test
    void shouldReturnEdgeAddedMessageDuringAdding() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase2-node1");
        resolver.resolve("ADD NODE phase2-node2");
        assertEquals("EDGE ADDED", resolver.resolve("ADD EDGE phase2-node1 phase2-node2 12"));
    }

    @Test
    void shouldReturnNodeNotFoundMessage() throws UnsupportedCommandException {
        assertEquals("ERROR: NODE NOT FOUND", resolver.resolve("ADD EDGE phase2-node1 phase2-node2 12"));
    }

    @Test
    void shouldReturnNodeRemovedMessage() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase2-node1");
        assertEquals("NODE REMOVED", resolver.resolve("REMOVE NODE phase2-node1"));
    }

    @Test
    void shouldReturnNodeNotFoundMessageDuringRemoving() throws UnsupportedCommandException {
        assertEquals("ERROR: NODE NOT FOUND", resolver.resolve("REMOVE NODE phase2-node1"));
    }

    @Test
    void shouldReturnEdgeRemovedMessage() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase2-node1");
        resolver.resolve("ADD NODE phase2-node2");
        resolver.resolve("ADD EDGE phase2-node1 phase2-node2 18");
        assertEquals("EDGE REMOVED", resolver.resolve("REMOVE EDGE phase2-node1 phase2-node2"));
    }

    @Test
    void shouldReturnNodeNotFoundMessageDuringEdgeRemoving() throws UnsupportedCommandException {
        assertEquals("ERROR: NODE NOT FOUND", resolver.resolve("REMOVE EDGE phase2-node1 phase2-node2"));
    }
}