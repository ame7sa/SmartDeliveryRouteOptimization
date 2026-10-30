package edu.aitu.delivery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Fifth strategy: prioritize low time while retaining the common hard constraints. */
public class PremiumRouteStrategy extends AbstractRouteStrategy {
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<String> rejected = new ArrayList<>();
        Route best = validRoutes(routes, request, rejected).stream()
                .min(Comparator.comparingInt(Route::effectiveMinutes)
                        .thenComparing(Comparator.comparingDouble(Route::distanceKm)))
                .orElseThrow();
        return new RouteDecision(best, getStrategyName(), best.effectiveMinutes(),
                "Premium service prioritizes the shortest effective time; distance breaks ties.", rejected);
    }
    public String getStrategyName() { return "Premium"; }
}
