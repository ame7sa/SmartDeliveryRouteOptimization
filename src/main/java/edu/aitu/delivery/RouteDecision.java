package edu.aitu.delivery;

import java.util.List;

public record RouteDecision(Route route, String strategyName, double score, String explanation, List<String> rejectedReasons) {
    public RouteDecision {
        if (route == null) throw new IllegalArgumentException("Selected route is required");
        rejectedReasons = List.copyOf(rejectedReasons);
    }
}
