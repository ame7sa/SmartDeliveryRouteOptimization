package edu.aitu.delivery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EcoFriendlyRouteStrategy extends AbstractRouteStrategy {
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<String> rejected = new ArrayList<>();
        Route best = validRoutes(routes, request, rejected).stream()
                .min(Comparator.comparingDouble(r -> ecoScore(r))).orElseThrow();
        return new RouteDecision(best, getStrategyName(), ecoScore(best),
                String.format("Lowest eco score: emissions %.2f kg × 0.8 + distance %.2f km × 0.2 = %.3f.", best.carbonEmissionKg(), best.distanceKm(), ecoScore(best)), rejected);
    }
    private double ecoScore(Route route) { return route.carbonEmissionKg() * 0.8 + route.distanceKm() * 0.2; }
    public String getStrategyName() { return "Eco-friendly"; }
}
