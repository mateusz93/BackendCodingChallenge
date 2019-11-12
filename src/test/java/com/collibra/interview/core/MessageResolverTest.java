package com.collibra.interview.core;

import com.collibra.interview.core.exception.UnsupportedCommandException;
import com.collibra.interview.graph.DirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.Instant;

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
            "REMOVE NODE phase2-node1", "REMOVE EDGE phase2-node1 phase2-node2",
            "SHORTEST PATH phase2-node1 phase2-node2"})
    void shouldVerifySupportedMessages(String message) {
        assertFalse(resolver.isNotSupported(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"HI, I AM", "HI, I AM NOT name", "BYE TOM!",
            "ADD NODE", "ADD EDGE", "ADD EDGE phase2-node1 phase2-node2 -1",
            "REMOVE NODE", "REMOVE EDGE phase2-node1", "REMOVE EDGE phase2-node1 phase2-node2 2",
            "SHORTEST PATH", "SHORTEST PATH phase2-node1"})
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
    void shouldVerifyTimerUpdating() {
        // given
        final Instant before = Instant.now();

        // when
        final Instant resolverTime = resolver.resetTimer();

        // then
        assertTrue((before.isBefore(resolverTime) || before.equals(resolverTime)) &&
                           (Instant.now().equals(resolverTime) || Instant.now().isAfter(resolverTime)));
    }

    @Test
    void shouldReturnWelcomeMessage() {
        final String welcomeMessage = resolver.getWelcomeMessage();
        assertTrue(welcomeMessage.matches("^HI, I AM [-a-zA-Z0-9]*$"));
    }

    @Test
    void shouldReturnTimeoutMessage() throws UnsupportedCommandException {
        resolver.resolve("HI, I AM John");
        assertEquals("BYE John, WE SPOKE FOR 300 MS", resolver.getTimeoutMessage(300));
    }

    @Test
    void shouldThrowExceptionDuringGettingTimeoutMessageWithoutClientName() {
        assertThrows(IllegalStateException.class, () -> resolver.getTimeoutMessage(300), "Can not generate timeout message without client name");
    }

    @Test
    void shouldReturnGoodbyeMessage() throws UnsupportedCommandException {
        resolver.resolve("HI, I AM John");
        final String result = resolver.resolve("BYE MATE!");
        assertTrue(result.matches("^BYE John, WE SPOKE FOR \\d+ MS*$"));
    }

    @Test
    void shouldThrowExceptionDuringGettingGoodbyeMessageWithoutClientName() {
        assertThrows(IllegalStateException.class, () -> resolver.resolve("BYE MATE!"), "Can not generate timeout message without client name");
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

    @Test
    void shouldCalculateTheShortestDistanceBetweenTwoExistingNodes() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase3-node1");
        resolver.resolve("ADD NODE phase3-node2");
        resolver.resolve("ADD NODE phase3-node3");
        resolver.resolve("ADD EDGE phase3-node1 phase3-node2 12");
        resolver.resolve("ADD EDGE phase3-node2 phase3-node3 4");
        assertEquals("16", resolver.resolve("SHORTEST PATH phase3-node1 phase3-node3"));
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenNonExistingNodes() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase3-node1");
        resolver.resolve("ADD NODE phase3-node2");
        resolver.resolve("ADD EDGE phase3-node1 phase3-node2 12");
        assertEquals("ERROR: NODE NOT FOUND", resolver.resolve("SHORTEST PATH phase3-node1 phase3-node3"));
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenNonConnectedNodes() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase3-node1");
        resolver.resolve("ADD NODE phase3-node2");
        resolver.resolve("ADD NODE phase3-node3");
        resolver.resolve("ADD EDGE phase3-node1 phase3-node2 12");
        assertEquals(String.valueOf(Integer.MAX_VALUE), resolver.resolve("SHORTEST PATH phase3-node1 phase3-node3"));
    }

    @Test
    void shouldFindAllCloserNodesThan() throws UnsupportedCommandException {
        resolver.resolve("ADD NODE phase3-node1");
        resolver.resolve("ADD NODE phase3-node2");
        resolver.resolve("ADD NODE phase3-node3");
        resolver.resolve("ADD NODE phase3-node4");
        resolver.resolve("ADD EDGE phase3-node1 phase3-node3 10");
        resolver.resolve("ADD EDGE phase3-node1 phase3-node2 12");
        resolver.resolve("ADD EDGE phase3-node2 phase3-node4 15");
        assertEquals("ERROR: NODE NOT FOUND", resolver.resolve("CLOSER THAN 5 phase3-node5"));
        assertEquals("phase3-node1", resolver.resolve("CLOSER THAN 0 phase3-node1"));
        assertEquals("phase3-node3", resolver.resolve("CLOSER THAN 11 phase3-node1"));
        assertEquals("phase3-node2,phase3-node3", resolver.resolve("CLOSER THAN 13 phase3-node1"));

    }
}