package edu.aitu.delivery;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractRouteStrategy implements RouteStrategy {
    protected List<Route> validRoutes(List<Route> routes, DeliveryRequest request, List<String> rejected) {
        if (routes == null || routes.isEmpty()) throw new NoSuitableRouteException("No candidate routes were provided.");
        if (request == null) throw new IllegalArgumentException("Delivery request is required");
        List<Route> valid = new ArrayList<>();
        for (Route route : routes) {
            String reason = rejectionReason(route, request);
            if (reason == null) valid.add(route);
            else rejected.add(route.id() + " rejected: " + reason);
        }
        if (valid.isEmpty()) throw new NoSuitableRouteException("No route satisfies the request constraints. " + String.join("; ", rejected));
        return valid;
    }
    private String rejectionReason(Route route, DeliveryRequest request) {
        if (route == null) return "missing route";
        if (!route.available()) return "route unavailable";
        if (route.prohibited()) return "route prohibited for this delivery";
        if (route.totalCost(request) > request.maxBudget()) return "budget exceeded";
        if (route.effectiveMinutes() > request.maxDeliveryMinutes()) return "deadline exceeded";
        if (route.carbonEmissionKg() > request.maxCarbonEmissionKg()) return "emission limit exceeded";
        return null;
    }
}
