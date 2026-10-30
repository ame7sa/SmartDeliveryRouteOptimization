package edu.aitu.delivery;

import java.util.List;

public final class DemoData {
    private DemoData() {}
    public static List<Route> routes() {
        Location warehouse = new Location("AITU Warehouse", 51.0900, 71.4180);
        Location customer = new Location("Customer", 51.1280, 71.4300);
        return List.of(
            new Route("R1", warehouse, customer, 8.0, 24, 2200, 2.8, 5, true, false),
            new Route("R2", warehouse, customer, 11.0, 18, 3000, 3.4, 2, true, false),
            new Route("R3", warehouse, customer, 6.5, 31, 1900, 1.3, 3, true, false),
            new Route("R4", warehouse, customer, 9.0, 27, 1600, 1.1, 1, true, false),
            new Route("R5", warehouse, customer, 12.0, 43, 900, 2.2, 0, true, false),
            new Route("R6", warehouse, customer, 7.5, 22, 2600, 1.8, 4, true, false),
            new Route("R7", warehouse, customer, 10.0, 35, 1500, 0.9, 2, true, false),
            new Route("R8", warehouse, customer, 5.0, 20, 500, 0.5, 0, false, false)
        );
    }
}
