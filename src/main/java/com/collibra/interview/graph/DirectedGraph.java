package com.collibra.interview.graph;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DirectedGraph {

    private static volatile DirectedGraph instance;
    private final DirectedWeightedMultigraph<Node, Edge> graph;

    private DirectedGraph() {
        if (instance != null) {
            throw new IllegalStateException("Already initialized");
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

    /**
     * Add egde to graph
     *
     * @param edge      edge to add
     * @return boolean  {@code true} if edge added correctly or if nodes of edge are the same
     *                  {@code false} if any of the edge nodes does not exist
     */
    public synchronized boolean addEdge(final Edge edge) {
        if (!graph.containsVertex(edge.getSource()) || !graph.containsVertex(edge.getTarget())) {
            return false;
        }
        if (edge.getSource().equals(edge.getTarget())) {
            return true;
        }
        graph.addEdge(edge.getSource(), edge.getTarget(), edge);
        graph.setEdgeWeight(edge, edge.getWeight());
        return true;
    }

    public synchronized boolean removeNode(final Node node) {
        return graph.removeVertex(node);
    }

    public synchronized boolean removeEdge(final Node source, final Node target) {
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            return false;
        }
        graph.removeAllEdges(source, target);
        return true;
    }

    /**
     * Calculate the shortest path between two nodes in the directed graph
     *
     * @param source    source node
     * @param target    target node
     * @return int      sum of the the shortest weights
     *                  {@code -1} if node not exists
     *                  {@code Integer.MAX_VALUE} if not exists connection between nodes
     */
    public synchronized int findTheShortestPath(Node source, Node target) {
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            return -1;
        }
        GraphPath<Node, Edge> shortestPath = DijkstraShortestPath.findPathBetween(graph, source, target);
        if (shortestPath == null) {
            return Integer.MAX_VALUE;
        }
        return shortestPath.getEdgeList()
                           .stream()
                           .map(Edge::getWeight)
                           .reduce(0, Integer::sum);
    }

    /**
     * Method finds all the nodes that are closer to node than the given weight.
     *
     * @param node          node
     * @param weight        weight
     * @throws IllegalArgumentException if node not exists
     * @return List<Node>   all nodes that are closer to node than the given weight
     */
    public synchronized List<Node> findAllCloserNodesThan(final Node node, final int weight) {
        if (!graph.containsVertex(node)) {
            throw new IllegalArgumentException("Node does not exist");
        }
        final ConnectivityInspector inspector = new ConnectivityInspector(graph);
        return HashSet.ofAll(inspector.connectedSetOf(node))
                      .filter(n -> weight > findTheShortestPath(node, (Node) n))
                      .filter(n -> !n.equals(node))
                      .toList();
    }
}
