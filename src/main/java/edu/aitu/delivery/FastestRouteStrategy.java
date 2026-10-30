package edu.aitu.delivery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FastestRouteStrategy extends AbstractRouteStrategy {
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<String> rejected = new ArrayList<>();
        Route best = validRoutes(routes, request, rejected).stream()
                .min(Comparator.comparingInt(Route::effectiveMinutes).thenComparingDouble(r -> r.totalCost(request)))
                .orElseThrow();
        return new RouteDecision(best, getStrategyName(), best.effectiveMinutes(),
                "Minimum effective delivery time: " + best.estimatedMinutes() + " min + " + best.trafficPenaltyMinutes() + " min traffic penalty = " + best.effectiveMinutes() + " min.", rejected);
    }
    public String getStrategyName() { return "Fastest"; }
}
