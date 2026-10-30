package edu.aitu.delivery;

import java.util.ArrayList;
import java.util.List;

public class RouteComparison {
    public List<RouteDecision> compare(List<Route> routes, DeliveryRequest request, List<RouteStrategy> strategies) {
        List<RouteDecision> results = new ArrayList<>();
        for (RouteStrategy strategy : strategies) results.add(strategy.selectRoute(routes, request));
        return List.copyOf(results);
    }
}
