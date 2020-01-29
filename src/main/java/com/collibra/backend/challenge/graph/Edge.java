package com.collibra.backend.challenge.graph;

import lombok.Builder;
import lombok.Value;

import static com.google.common.base.Preconditions.checkArgument;

@Value
public class Edge {

    private final int weight;
    private final Node source;
    private final Node target;

    @Builder
    public Edge(final int weight, final Node source, final Node target) {
        checkArgument(weight > 0, "Weight must be positive: %s", weight);
        this.weight = weight;
        this.source = source;
        this.target = target;
    }
}
