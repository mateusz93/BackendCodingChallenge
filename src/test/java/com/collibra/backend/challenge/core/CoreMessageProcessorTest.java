package com.collibra.backend.challenge.core;

import com.collibra.backend.challenge.core.node.NodeAlreadyExistsException;
import com.collibra.backend.challenge.core.node.NodeNotFoundException;
import com.collibra.backend.challenge.graph.DirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreMessageProcessorTest {

    private CoreMessageProcessor messageProcessor;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field instance = DirectedGraph.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        messageProcessor = new CoreMessageProcessor(DirectedGraph.getInstance(), UUID.randomUUID(), Instant.now());
    }

    @Test
    void shouldThrowUnsupportedException() {
        assertThrows(MessageProcessingException.class, () -> messageProcessor.process("UNKNOWN"), "SORRY, I DID NOT UNDERSTAND THAT");
    }

    @Test
    void shouldReturnGreetingsMessage() throws MessageProcessingException {
        assertEquals("HI John", messageProcessor.process("HI, I AM John"));
    }

    @Test
    void shouldReturnWelcomeMessage() {
        UUID serverId = UUID.randomUUID();
        messageProcessor = new CoreMessageProcessor(DirectedGraph.getInstance(), serverId, Instant.now());
        assertEquals("HI, I AM " + serverId.toString(), messageProcessor.getWelcomeMessage());
    }

    @Test
    void shouldReturnTimeoutMessage() throws MessageProcessingException {
        messageProcessor.process("HI, I AM John");
        assertEquals("BYE John, WE SPOKE FOR 30000 MS", messageProcessor.getTimeoutMessage());
    }

    @Test
    void shouldThrowExceptionDuringGettingTimeoutMessageWithoutClientName() {
        assertThrows(IllegalStateException.class, () -> messageProcessor.getTimeoutMessage(), "Can not generate timeout message without client name");
    }

    @Test
    void shouldReturnGoodbyeMessage() throws MessageProcessingException {
        messageProcessor.process("HI, I AM John");
        final String result = messageProcessor.process("BYE MATE!");
        assertTrue(result.matches("^BYE John, WE SPOKE FOR \\d+ MS*$"));
    }

    @Test
    void shouldThrowExceptionDuringGettingGoodbyeMessageWithoutClientName() {
        assertThrows(IllegalStateException.class, () -> messageProcessor.process("BYE MATE!"), "Can not generate timeout message without client name");
    }

    @Test
    void shouldReturnNodeAddedMessage() throws MessageProcessingException {
        assertEquals("NODE ADDED", messageProcessor.process("ADD NODE phase2-node1"));
    }

    @Test
    void shouldReturnNodeAlreadyExistsMessage() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase2-node1");
        assertThrows(NodeAlreadyExistsException.class, () -> messageProcessor.process("ADD NODE phase2-node1"), "ERROR: NODE ALREADY EXISTS");
    }

    @Test
    void shouldReturnEdgeAddedMessageDuringAdding() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase2-node1");
        messageProcessor.process("ADD NODE phase2-node2");
        assertEquals("EDGE ADDED", messageProcessor.process("ADD EDGE phase2-node1 phase2-node2 12"));
    }

    @Test
    void shouldReturnEdgeAddedMessageDuringAddingEdgeWithTwoSameNodes() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase2-node1");
        assertEquals("EDGE ADDED", messageProcessor.process("ADD EDGE phase2-node1 phase2-node1 5"));
    }

    @Test
    void shouldReturnNodeNotFoundMessage() {
        assertThrows(NodeNotFoundException.class, () -> messageProcessor.process("ADD EDGE phase2-node1 phase2-node2 12"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldReturnNodeRemovedMessage() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase2-node1");
        assertEquals("NODE REMOVED", messageProcessor.process("REMOVE NODE phase2-node1"));
    }

    @Test
    void shouldReturnNodeNotFoundMessageDuringRemoving() {
        assertThrows(NodeNotFoundException.class, () -> messageProcessor.process("REMOVE NODE phase2-node1"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldReturnEdgeRemovedMessage() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase2-node1");
        messageProcessor.process("ADD NODE phase2-node2");
        messageProcessor.process("ADD EDGE phase2-node1 phase2-node2 18");
        assertEquals("EDGE REMOVED", messageProcessor.process("REMOVE EDGE phase2-node1 phase2-node2"));
    }

    @Test
    void shouldThrowExceptionDuringCreatingEdgeWithNonPositiveWeight() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase2-node1");
        messageProcessor.process("ADD NODE phase2-node2");
        assertThrows(IllegalArgumentException.class, () -> messageProcessor.process("ADD EDGE phase2-node1 phase2-node2 0"), "Weight must be positive: 0");
        assertThrows(MessageProcessingException.class, () -> messageProcessor.process("ADD EDGE phase2-node1 phase2-node2 -1"), "SORRY, I DID NOT UNDERSTAND THAT");
    }

    @Test
    void shouldReturnNodeNotFoundMessageDuringEdgeRemoving() {
        assertThrows(NodeNotFoundException.class, () -> messageProcessor.process("REMOVE EDGE phase2-node1 phase2-node2"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenTwoExistingNodes() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase3-node1");
        messageProcessor.process("ADD NODE phase3-node2");
        messageProcessor.process("ADD NODE phase3-node3");
        messageProcessor.process("ADD EDGE phase3-node1 phase3-node2 12");
        messageProcessor.process("ADD EDGE phase3-node2 phase3-node3 4");
        assertEquals("16", messageProcessor.process("SHORTEST PATH phase3-node1 phase3-node3"));
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenNonExistingNodes() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase3-node1");
        messageProcessor.process("ADD NODE phase3-node2");
        messageProcessor.process("ADD EDGE phase3-node1 phase3-node2 12");
        assertThrows(NodeNotFoundException.class, () -> messageProcessor.process("SHORTEST PATH phase3-node1 phase3-node3"), "ERROR: NODE NOT FOUND");
    }

    @Test
    void shouldCalculateTheShortestDistanceBetweenNonConnectedNodes() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase3-node1");
        messageProcessor.process("ADD NODE phase3-node2");
        messageProcessor.process("ADD NODE phase3-node3");
        messageProcessor.process("ADD EDGE phase3-node1 phase3-node2 12");
        assertEquals(String.valueOf(Integer.MAX_VALUE), messageProcessor.process("SHORTEST PATH phase3-node1 phase3-node3"));
    }

    @Test
    void shouldFindAllCloserNodesThan() throws MessageProcessingException {
        messageProcessor.process("ADD NODE phase3-node1");
        messageProcessor.process("ADD NODE phase3-node2");
        messageProcessor.process("ADD NODE phase3-node3");
        messageProcessor.process("ADD NODE phase3-node4");
        messageProcessor.process("ADD EDGE phase3-node1 phase3-node3 10");
        messageProcessor.process("ADD EDGE phase3-node1 phase3-node2 12");
        messageProcessor.process("ADD EDGE phase3-node2 phase3-node4 15");
        assertThrows(NodeNotFoundException.class, () -> messageProcessor.process("CLOSER THAN 5 phase3-node5"), "ERROR: NODE NOT FOUND");
        assertEquals("", messageProcessor.process("CLOSER THAN 0 phase3-node1"));
        assertEquals("phase3-node3", messageProcessor.process("CLOSER THAN 11 phase3-node1"));
        assertEquals("phase3-node2,phase3-node3", messageProcessor.process("CLOSER THAN 13 phase3-node1"));

    }
}