package edu.aitu.delivery;

public record DeliveryRequest(
        String requestId,
        DeliveryPriority priority,
        double maxBudget,
        int maxDeliveryMinutes,
        boolean ecoPreferred,
        double maxCarbonEmissionKg) {
    public DeliveryRequest {
        if (requestId == null || requestId.isBlank()) throw new IllegalArgumentException("Request ID is required");
        if (priority == null) throw new IllegalArgumentException("Priority is required");
        if (!Double.isFinite(maxBudget) || maxBudget < 0) throw new IllegalArgumentException("Budget must be non-negative");
        if (maxDeliveryMinutes <= 0) throw new IllegalArgumentException("Deadline must be positive");
        if (!Double.isFinite(maxCarbonEmissionKg) || maxCarbonEmissionKg < 0) throw new IllegalArgumentException("Emission limit must be non-negative");
    }
    public static DeliveryRequest standard(String id) {
        return new DeliveryRequest(id, DeliveryPriority.STANDARD, 10000, 180, false, 1000);
    }
}
