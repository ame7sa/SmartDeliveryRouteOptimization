package edu.aitu.delivery;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class StrategySelector {
    private final Map<DeliveryPriority, Supplier<RouteStrategy>> strategies = new EnumMap<>(DeliveryPriority.class);
    public StrategySelector() {
        strategies.put(DeliveryPriority.URGENT, FastestRouteStrategy::new);
        strategies.put(DeliveryPriority.ECONOMY, CheapestRouteStrategy::new);
        strategies.put(DeliveryPriority.ECO, EcoFriendlyRouteStrategy::new);
        strategies.put(DeliveryPriority.STANDARD, () -> new BalancedRouteStrategy(0.40, 0.30, 0.10, 0.20));
        strategies.put(DeliveryPriority.PREMIUM, PremiumRouteStrategy::new);
    }
    public void register(DeliveryPriority priority, Supplier<RouteStrategy> factory) {
        strategies.put(Objects.requireNonNull(priority), Objects.requireNonNull(factory));
    }
    public RouteStrategy select(DeliveryRequest request) {
        Objects.requireNonNull(request, "Delivery request is required");
        Supplier<RouteStrategy> factory = strategies.get(request.priority());
        if (factory == null) throw new IllegalArgumentException("No strategy configured for priority: " + request.priority());
        return factory.get();
    }
}
