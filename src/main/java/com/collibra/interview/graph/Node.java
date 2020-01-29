package com.collibra.interview.graph;

import lombok.Value;

@Value
public class Node implements Comparable<Node> {

    private final String name;

    @Override
    public int compareTo(Node node) {
        return name.compareTo(node.getName());
    }
}
