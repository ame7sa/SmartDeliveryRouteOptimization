package edu.aitu.delivery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BalancedRouteStrategy extends AbstractRouteStrategy {
    private final double timeWeight, costWeight, distanceWeight, emissionWeight;
    public BalancedRouteStrategy(double timeWeight, double costWeight, double distanceWeight, double emissionWeight) {
        double[] weights = {timeWeight, costWeight, distanceWeight, emissionWeight};
        double sum = 0;
        for (double w : weights) {
            if (!Double.isFinite(w) || w < 0) throw new IllegalArgumentException("Weights must be finite and non-negative");
            sum += w;
        }
        if (Math.abs(sum - 1.0) > 1e-9) throw new IllegalArgumentException("Balanced strategy weights must sum to 1.0");
        this.timeWeight = timeWeight; this.costWeight = costWeight;
        this.distanceWeight = distanceWeight; this.emissionWeight = emissionWeight;
    }
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<String> rejected = new ArrayList<>();
        List<Route> valid = validRoutes(routes, request, rejected);
        double maxTime = valid.stream().mapToDouble(Route::effectiveMinutes).max().orElse(1);
        double maxCost = valid.stream().mapToDouble(r -> r.totalCost(request)).max().orElse(1);
        double maxDistance = valid.stream().mapToDouble(Route::distanceKm).max().orElse(1);
        double maxEmission = valid.stream().mapToDouble(Route::carbonEmissionKg).max().orElse(1);
        Route best = valid.stream().min(Comparator.comparingDouble(r -> score(r, request, maxTime, maxCost, maxDistance, maxEmission))).orElseThrow();
        double score = score(best, request, maxTime, maxCost, maxDistance, maxEmission);
        return new RouteDecision(best, getStrategyName(), score,
                String.format("Weighted normalized score %.3f (time %.2f, cost %.2f, distance %.2f, emissions %.2f). Lower is better.", score, timeWeight, costWeight, distanceWeight, emissionWeight), rejected);
    }
    private double score(Route r, DeliveryRequest req, double mt, double mc, double md, double me) {
        return ratio(r.effectiveMinutes(), mt) * timeWeight + ratio(r.totalCost(req), mc) * costWeight
                + ratio(r.distanceKm(), md) * distanceWeight + ratio(r.carbonEmissionKg(), me) * emissionWeight;
    }
    private double ratio(double value, double max) { return max == 0 ? 0 : value / max; }
    public String getStrategyName() { return "Balanced"; }
}
