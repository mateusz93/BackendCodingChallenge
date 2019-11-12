package com.collibra.interview.graph;

import lombok.Value;

@Value
public class Node implements Comparable {

    private final String name;

    @Override
    public int compareTo(Object o) {
        return name.compareTo(o.toString());
    }
}
