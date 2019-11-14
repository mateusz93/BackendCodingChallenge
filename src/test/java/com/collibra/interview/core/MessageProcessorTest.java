package com.collibra.interview.core;

import com.collibra.interview.exception.MessageProcessingException;
import com.collibra.interview.exception.NodeAlreadyExistsException;
import com.collibra.interview.exception.NodeNotFoundException;
import com.collibra.interview.graph.DirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageProcessorTest {

    private MessageProcessor resolver;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field instance = DirectedGraph.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        resolver = new MessageProcessor(DirectedGraph.getInstance(), Instant.now());
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
        assertThrows(MessageProcessingException.class, () -> resolver.process("UNKNOWN"), "SORRY, I DID NOT UNDERSTAND THAT");
    }

    @Test
    void shouldReturnGreetingsMessage() throws MessageProcessingException {
        assertEquals("HI John", resolver.process("HI, I AM John"));
    }

    @Test
    void shouldReturnWelcomeMessage() {
        final String name = UUID.randomUUID().toString();
        assertEquals("HI, I AM " + name, resolver.getWelcomeMessage(name));
    }

    @Test
    void shouldReturnTimeoutMessage() throws MessageProcessingException {
        resolver.process("HI, I AM John");
        assertEquals("BYE John, WE SPOKE FOR 300 MS", resolver.getTimeoutMessage(300));
    }

    @Test
    void shouldThrowExceptionDuringGettingTimeoutMessageWithoutClientName() {
        assertThrows(IllegalStateException.class, () -> resolver.getTimeoutMessage(300), "Can not generate timeout message without client name");
    }

    @Test
    void shouldReturnGoodbyeMessage() throws MessageProcessingException {
        resolver.process("HI, I AM John");
        final String result = resolver.process("BYE MATE!");
        assertTrue(result.matches("^BYE John, WE SPOKE FOR \\d+ MS*$"));
    }

    @Test
    void shouldThrowExceptionDuringGettingGoodbyeMessageWithoutClientName() {
        assertThrows(IllegalStateException.class, () -> resolver.process("BYE MATE!"), "Can not generate timeout message without client name");
    }

    @Test
    void shouldReturnNodeAddedMessage() throws MessageProcessingException {
        assertEquals("NODE ADDED", resolver.process("ADD NODE phase2-node1"));
    }

    @Test
    void shouldReturnNodeAlreadyExistsMessage() throws MessageProcessingException {
        resolver.process("ADD NODE phase2-node1");
        assertThrows(NodeAlreadyExistsException.class, () -> resolver.process("ADD NODE phase2-node1"), "ERROR: NODE ALREADY EXISTS");
    }

    @Test
    void shouldReturnEdgeAddedMessageDuringAdding() throws MessageProcessingException {
        resolver.process("ADD NODE phase2-node1");
        resolver.process("ADD NODE phase2-node2");
        assertEquals("EDGE ADDED", resolver.process("ADD EDGE phase2-node1 phase2-node2 12"));
    }

    @Test
    void shouldReturnEdgeAddedMessageDuringAddingEdgeWithTwoSameNodes() throws MessageProcessingException {
        resolver.process("ADD NODE phase2-node1");
        assertEquals("EDGE ADDED", resolver.process("ADD EDGE phase2-node1 phase2-node1 5"));
    }

    @Test
    void shouldReturnNodeNotFoundMessage() {
        assertThrows(NodeNotFoundException.class, () -> resolver.process("ADD EDGE phase2-node1 phase2-node2 12"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldReturnNodeRemovedMessage() throws MessageProcessingException {
        resolver.process("ADD NODE phase2-node1");
        assertEquals("NODE REMOVED", resolver.process("REMOVE NODE phase2-node1"));
    }

    @Test
    void shouldReturnNodeNotFoundMessageDuringRemoving() {
        assertThrows(NodeNotFoundException.class, () -> resolver.process("REMOVE NODE phase2-node1"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldReturnEdgeRemovedMessage() throws MessageProcessingException {
        resolver.process("ADD NODE phase2-node1");
        resolver.process("ADD NODE phase2-node2");
        resolver.process("ADD EDGE phase2-node1 phase2-node2 18");
        assertEquals("EDGE REMOVED", resolver.process("REMOVE EDGE phase2-node1 phase2-node2"));
    }

    @Test
    void shouldThrowExceptionDuringCreatingEdgeWithNonPositiveWeight() throws MessageProcessingException {
        resolver.process("ADD NODE phase2-node1");
        resolver.process("ADD NODE phase2-node2");
        assertThrows(IllegalArgumentException.class, () -> resolver.process("ADD EDGE phase2-node1 phase2-node2 0"), "Weight must be positive: 0");
        assertThrows(MessageProcessingException.class, () -> resolver.process("ADD EDGE phase2-node1 phase2-node2 -1"), "SORRY, I DID NOT UNDERSTAND THAT");
    }

    @Test
    void shouldReturnNodeNotFoundMessageDuringEdgeRemoving() {
        assertThrows(NodeNotFoundException.class, () -> resolver.process("REMOVE EDGE phase2-node1 phase2-node2"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenTwoExistingNodes() throws MessageProcessingException {
        resolver.process("ADD NODE phase3-node1");
        resolver.process("ADD NODE phase3-node2");
        resolver.process("ADD NODE phase3-node3");
        resolver.process("ADD EDGE phase3-node1 phase3-node2 12");
        resolver.process("ADD EDGE phase3-node2 phase3-node3 4");
        assertEquals("16", resolver.process("SHORTEST PATH phase3-node1 phase3-node3"));
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenNonExistingNodes() throws MessageProcessingException {
        resolver.process("ADD NODE phase3-node1");
        resolver.process("ADD NODE phase3-node2");
        resolver.process("ADD EDGE phase3-node1 phase3-node2 12");
        assertThrows(NodeNotFoundException.class, () -> resolver.process("SHORTEST PATH phase3-node1 phase3-node3"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenNonConnectedNodes() throws MessageProcessingException {
        resolver.process("ADD NODE phase3-node1");
        resolver.process("ADD NODE phase3-node2");
        resolver.process("ADD NODE phase3-node3");
        resolver.process("ADD EDGE phase3-node1 phase3-node2 12");
        assertEquals(String.valueOf(Integer.MAX_VALUE), resolver.process("SHORTEST PATH phase3-node1 phase3-node3"));
    }

    @Test
    void shouldFindAllCloserNodesThan() throws MessageProcessingException {
        resolver.process("ADD NODE phase3-node1");
        resolver.process("ADD NODE phase3-node2");
        resolver.process("ADD NODE phase3-node3");
        resolver.process("ADD NODE phase3-node4");
        resolver.process("ADD EDGE phase3-node1 phase3-node3 10");
        resolver.process("ADD EDGE phase3-node1 phase3-node2 12");
        resolver.process("ADD EDGE phase3-node2 phase3-node4 15");
        assertThrows(NodeNotFoundException.class, () -> resolver.process("CLOSER THAN 5 phase3-node5"), "ERROR: NODE NOT FOUND");
        assertEquals("", resolver.process("CLOSER THAN 0 phase3-node1"));
        assertEquals("phase3-node3", resolver.process("CLOSER THAN 11 phase3-node1"));
        assertEquals("phase3-node2,phase3-node3", resolver.process("CLOSER THAN 13 phase3-node1"));

    }
}