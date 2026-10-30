package edu.aitu.delivery;

public record Route(
        String id,
        Location origin,
        Location destination,
        double distanceKm,
        int estimatedMinutes,
        double baseCost,
        double carbonEmissionKg,
        int trafficPenaltyMinutes,
        boolean available,
        boolean prohibited) {
    public Route {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Route ID is required");
        if (origin == null || destination == null) throw new IllegalArgumentException("Origin and destination are required");
        if (!Double.isFinite(distanceKm) || distanceKm < 0) throw new IllegalArgumentException("Distance must be non-negative");
        if (estimatedMinutes < 0 || trafficPenaltyMinutes < 0) throw new IllegalArgumentException("Time values must be non-negative");
        if (!Double.isFinite(baseCost) || baseCost < 0) throw new IllegalArgumentException("Cost must be non-negative");
        if (!Double.isFinite(carbonEmissionKg) || carbonEmissionKg < 0) throw new IllegalArgumentException("Emission must be non-negative");
    }
    public int effectiveMinutes() { return estimatedMinutes + trafficPenaltyMinutes; }
    public double totalCost(DeliveryRequest request) {
        double prioritySurcharge = request.priority() == DeliveryPriority.URGENT ? 800.0 : 0.0;
        double distanceCost = distanceKm * 20.0;
        return baseCost + distanceCost + prioritySurcharge;
    }
}
