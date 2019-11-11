package com.collibra.interview.graph;

import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.Set;

public class DirectedGraph {

    private static volatile DirectedGraph instance;
    private final DirectedWeightedMultigraph<Node, Edge> graph;

    private DirectedGraph() {
        if (instance != null) {
            throw new IllegalStateException("Already initialized.");
        }
        graph = new DirectedWeightedMultigraph<>(Edge.class);
    }

    public static DirectedGraph getInstance() {
        DirectedGraph result = instance;
        if (result == null) {
            synchronized (DirectedGraph.class) {
                result = instance;
                if (result == null) {
                    instance = result = new DirectedGraph();
                }
            }
        }
        return result;
    }

    public synchronized boolean addNode(final Node node) {
        return graph.addVertex(node);
    }

    public synchronized boolean addEdge(final Edge edge) {
        try {
            if (graph.addEdge(edge.getSource(), edge.getTarget(), edge)) {
                graph.setEdgeWeight(edge, edge.getWeight());
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public synchronized boolean removeNode(final Node node) {
        return graph.removeVertex(node);
    }

    public synchronized boolean removeEdge(final Node source, final Node target) {
        final Set<Edge> removedEdges = graph.removeAllEdges(source, target);
        return removedEdges != null && !removedEdges.isEmpty();
    }
}
