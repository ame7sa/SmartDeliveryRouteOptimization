package edu.aitu.delivery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CheapestRouteStrategy extends AbstractRouteStrategy {
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<String> rejected = new ArrayList<>();
        Route best = validRoutes(routes, request, rejected).stream()
                .min(Comparator.comparingDouble(r -> r.totalCost(request))).orElseThrow();
        return new RouteDecision(best, getStrategyName(), best.totalCost(request),
                String.format("Minimum total cost: base %.2f + distance %.2f/km × 20 + priority surcharge = %.2f.", best.baseCost(), best.distanceKm(), best.totalCost(request)), rejected);
    }
    public String getStrategyName() { return "Cheapest"; }
}
