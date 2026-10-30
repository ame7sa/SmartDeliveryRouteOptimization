package edu.aitu.delivery;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Route> routes = DemoData.routes();
        DeliveryRequest request = new DeliveryRequest("DEL-105", DeliveryPriority.STANDARD, 5000, 60, false, 5.0);
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        System.out.println("SMART DELIVERY & ROUTE OPTIMIZATION");
        System.out.println("Request: " + request.requestId() + " | priority=" + request.priority());
        print(planner.plan(routes, request));
        planner.setStrategy(new CheapestRouteStrategy()); print(planner.plan(routes, request));
        planner.setStrategy(new EcoFriendlyRouteStrategy()); print(planner.plan(routes, request));
        planner.setStrategy(new BalancedRouteStrategy(0.40, 0.30, 0.10, 0.20)); print(planner.plan(routes, request));
        System.out.println("\nAUTOMATIC STRATEGY SELECTION");
        RouteStrategy selected = new StrategySelector().select(request);
        System.out.println("Selected strategy: " + selected.getStrategyName());
        print(new RoutePlanner(selected).plan(routes, request));
        System.out.println("\nSTRATEGY COMPARISON");
        List<RouteDecision> decisions = new RouteComparison().compare(routes, request, List.of(
                new FastestRouteStrategy(), new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(), new BalancedRouteStrategy(0.40, 0.30, 0.10, 0.20)));
        decisions.forEach(Main::print);
    }
    private static void print(RouteDecision d) {
        System.out.printf("%s -> %s | time=%d min | cost=%.2f | CO2=%.2f kg | score=%.3f%n",
                d.strategyName(), d.route().id(), d.route().effectiveMinutes(),
                d.route().totalCost(new DeliveryRequest("DISPLAY", DeliveryPriority.STANDARD, 100000, 10000, false, 10000)),
                d.route().carbonEmissionKg(), d.score());
        System.out.println("  Reason: " + d.explanation());
        d.rejectedReasons().forEach(reason -> System.out.println("  " + reason));
    }
}
