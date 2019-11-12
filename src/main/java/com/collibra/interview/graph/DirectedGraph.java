package com.collibra.interview.graph;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.apache.commons.collections4.CollectionUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DirectedGraph {

    private static volatile DirectedGraph instance;
    private final SimpleDirectedWeightedGraph<Node, Edge> graph;

    private DirectedGraph() {
        if (instance != null) {
            throw new IllegalStateException("Already initialized.");
        }
        graph = new SimpleDirectedWeightedGraph<>(Edge.class);
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
        if (!graph.containsVertex(edge.getSource()) || !graph.containsVertex(edge.getTarget())) {
            return false;
        }
        removeEdge(edge.getSource(), edge.getTarget());
        if (graph.addEdge(edge.getSource(), edge.getTarget(), edge)) {
            graph.setEdgeWeight(edge, edge.getWeight());
            return true;
        }
        return false;
    }

    public synchronized boolean removeNode(final Node node) {
        return graph.removeVertex(node);
    }

    public synchronized boolean removeEdge(final Node source, final Node target) {
        return CollectionUtils.isNotEmpty(graph.removeAllEdges(source, target));
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
            throw new IllegalArgumentException("Node not exists");
        }
        final ConnectivityInspector inspector = new ConnectivityInspector(graph);
        return HashSet.ofAll(inspector.connectedSetOf(node))
                      .filter(n -> weight > findTheShortestPath(node, (Node) n))
                      .filter(n -> !n.equals(node))
                      .toList();
    }
}
