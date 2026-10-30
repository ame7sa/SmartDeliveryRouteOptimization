package edu.aitu.delivery;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RouteStrategyTest {
    private final Location a = new Location("A", 51.0, 71.0);
    private final Location b = new Location("B", 51.1, 71.1);
    private Route r(String id, double km, int min, double cost, double co2, int traffic, boolean available, boolean prohibited) {
        return new Route(id, a, b, km, min, cost, co2, traffic, available, prohibited);
    }
    private DeliveryRequest req(DeliveryPriority p, double budget, int deadline, double co2) {
        return new DeliveryRequest("T1", p, budget, deadline, false, co2);
    }
    private List<Route> sample() {
        return List.of(r("FAST", 8, 20, 1000, 4, 2, true, false),
                r("CHEAP", 5, 35, 500, 2, 0, true, false),
                r("ECO", 7, 30, 800, 0.5, 1, true, false));
    }
    @Test void fastestUsesTrafficAdjustedTime() {
        assertEquals("FAST", new FastestRouteStrategy().selectRoute(sample(), req(DeliveryPriority.STANDARD, 5000, 60, 10)).route().id());
    }
    @Test void fastestConsidersTrafficPenalty() {
        var routes = List.of(r("A", 1, 10, 100, 1, 20, true, false), r("B", 2, 15, 100, 1, 0, true, false));
        assertEquals("B", new FastestRouteStrategy().selectRoute(routes, req(DeliveryPriority.STANDARD, 5000, 60, 10)).route().id());
    }
    @Test void cheapestChoosesMinimumTotalCost() {
        assertEquals("CHEAP", new CheapestRouteStrategy().selectRoute(sample(), req(DeliveryPriority.ECONOMY, 5000, 60, 10)).route().id());
    }
    @Test void urgentSurchargeIsIncludedInCost() {
        Route route = sample().get(0);
        assertEquals(1160.0, route.totalCost(req(DeliveryPriority.STANDARD, 5000, 60, 10)));
        assertEquals(1960.0, route.totalCost(req(DeliveryPriority.URGENT, 5000, 60, 10)));
    }
    @Test void ecoChoosesLowestWeightedImpact() {
        assertEquals("ECO", new EcoFriendlyRouteStrategy().selectRoute(sample(), req(DeliveryPriority.ECO, 5000, 60, 10)).route().id());
    }
    @Test void balancedReturnsValidRoute() {
        assertTrue(sample().stream().anyMatch(r -> r.id().equals(new BalancedRouteStrategy(.4,.3,.1,.2).selectRoute(sample(), req(DeliveryPriority.STANDARD,5000,60,10)).route().id())));
    }
    @Test void balancedWeightsMustSumToOne() {
        assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(.4,.3,.1,.1));
    }
    @Test void negativeBalancedWeightRejected() {
        assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(-.1,.5,.3,.3));
    }
    @Test void runtimeSwitchingUsesSamePlanner() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        String first = planner.plan(sample(), req(DeliveryPriority.STANDARD,5000,60,10)).route().id();
        planner.setStrategy(new CheapestRouteStrategy());
        String second = planner.plan(sample(), req(DeliveryPriority.STANDARD,5000,60,10)).route().id();
        assertNotEquals(first, second);
    }
    @Test void selectorMapsUrgentToFastest() {
        assertInstanceOf(FastestRouteStrategy.class, new StrategySelector().select(req(DeliveryPriority.URGENT,5000,60,10)));
    }
    @Test void selectorMapsEconomyToCheapest() {
        assertInstanceOf(CheapestRouteStrategy.class, new StrategySelector().select(req(DeliveryPriority.ECONOMY,5000,60,10)));
    }
    @Test void selectorMapsEcoToEcoStrategy() {
        assertInstanceOf(EcoFriendlyRouteStrategy.class, new StrategySelector().select(req(DeliveryPriority.ECO,5000,60,10)));
    }
    @Test void unavailableRouteIsFiltered() {
        var routes = List.of(r("NO",1,1,1,1,0,false,false), r("YES",3,20,200,2,0,true,false));
        assertEquals("YES", new FastestRouteStrategy().selectRoute(routes, req(DeliveryPriority.STANDARD,5000,60,10)).route().id());
    }
    @Test void budgetConstraintFiltersRoute() {
        var routes = List.of(r("EXPENSIVE",1,1,5000,1,0,true,false), r("OK",4,20,100,2,0,true,false));
        assertEquals("OK", new CheapestRouteStrategy().selectRoute(routes, req(DeliveryPriority.STANDARD,300,60,10)).route().id());
    }
    @Test void deadlineConstraintFiltersRoute() {
        var routes = List.of(r("LATE",1,100,100,1,0,true,false), r("ON-TIME",4,20,100,2,0,true,false));
        assertEquals("ON-TIME", new FastestRouteStrategy().selectRoute(routes, req(DeliveryPriority.STANDARD,5000,30,10)).route().id());
    }
    @Test void emissionConstraintFiltersRoute() {
        var routes = List.of(r("DIRTY",1,10,100,9,0,true,false), r("CLEAN",4,20,100,1,0,true,false));
        assertEquals("CLEAN", new EcoFriendlyRouteStrategy().selectRoute(routes, req(DeliveryPriority.ECO,5000,60,2)).route().id());
    }
    @Test void prohibitedRouteIsFiltered() {
        var routes = List.of(r("BAN",1,1,1,1,0,true,true), r("OK",4,20,100,2,0,true,false));
        assertEquals("OK", new FastestRouteStrategy().selectRoute(routes, req(DeliveryPriority.STANDARD,5000,60,10)).route().id());
    }
    @Test void noValidRouteThrowsExplicitException() {
        assertThrows(NoSuitableRouteException.class, () -> new FastestRouteStrategy().selectRoute(
                List.of(r("LATE",1,100,100,1,0,true,false)), req(DeliveryPriority.STANDARD,5000,10,10)));
    }
    @Test void emptyRouteListThrowsExplicitException() {
        assertThrows(NoSuitableRouteException.class, () -> new FastestRouteStrategy().selectRoute(List.of(), req(DeliveryPriority.STANDARD,5000,60,10)));
    }
    @Test void comparisonRunsAllStrategies() {
        var decisions = new RouteComparison().compare(sample(), req(DeliveryPriority.STANDARD,5000,60,10),
                List.of(new FastestRouteStrategy(), new CheapestRouteStrategy()));
        assertEquals(2, decisions.size());
        assertNotNull(decisions.get(0).route());
    }
    @Test void fifthPremiumStrategyWorks() {
        assertEquals("Premium", new PremiumRouteStrategy().getStrategyName());
        assertNotNull(new PremiumRouteStrategy().selectRoute(sample(), req(DeliveryPriority.PREMIUM,5000,60,10)).route());
    }
    @Test void negativeDistanceRejected() {
        assertThrows(IllegalArgumentException.class, () -> r("BAD",-1,10,100,1,0,true,false));
    }
    @Test void missingRequestRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FastestRouteStrategy().selectRoute(sample(), null));
    }
    @Test void boundaryBudgetIsAllowed() {
        Route route = r("EDGE",5,20,100,1,0,true,false);
        double exactBudget = route.totalCost(req(DeliveryPriority.STANDARD,1000,60,10));
        assertEquals("EDGE", new CheapestRouteStrategy().selectRoute(List.of(route), req(DeliveryPriority.STANDARD,exactBudget,60,10)).route().id());
    }
}
