# Assignment 5 — Smart Delivery & Route Optimization Engine
## Strategy Pattern

**Language:** Java 17  
**Build:** Maven  
**Testing:** JUnit 5

### 1. Objective
The goal is to implement a delivery route planner that supports several interchangeable optimization algorithms. The Strategy Pattern avoids a large conditional statement and separates the route-selection algorithm from the planner.

### 2. Architecture
`RoutePlanner` is the Context and stores a `RouteStrategy` reference. The interface defines the selection contract. `FastestRouteStrategy`, `CheapestRouteStrategy`, `EcoFriendlyRouteStrategy`, `BalancedRouteStrategy`, and `PremiumRouteStrategy` implement different algorithms. `StrategySelector` selects a strategy based on delivery priority. `RouteComparison` compares strategies using the same delivery request.

### 3. Domain model
`Location` describes a location; `Route` contains distance, estimated time, base cost, emissions, traffic penalty and availability; `DeliveryRequest` contains priority and hard constraints; `RouteDecision` stores the chosen route, score, explanation and rejection reasons.

### 4. Algorithms and formulas
- Effective time = estimated minutes + traffic penalty.
- Total cost = base cost + distance in km × 20 + urgent surcharge of 800.
- Eco score = carbon emissions × 0.8 + distance × 0.2.
- Balanced score = normalized time × 0.40 + normalized cost × 0.30 + normalized distance × 0.10 + normalized emissions × 0.20. The constructor validates that all weights are non-negative and their sum is 1.0.

Before ranking, each strategy rejects routes that are unavailable, prohibited, over budget, past the deadline, or above the request's emissions limit. If no route remains, `NoSuitableRouteException` is thrown with an explanation.

### 5. Runtime switching and automatic selection
The demo switches a single `RoutePlanner` instance among fastest, cheapest, eco-friendly, and balanced strategies. The selector maps URGENT to fastest, ECONOMY to cheapest, ECO to eco-friendly, STANDARD to balanced, and PREMIUM to the fifth strategy.

### 6. Strategy comparison and explanation
The comparison component runs a list of strategies against the same request. Each decision includes a reason and any rejected route reasons, allowing the user to understand why a route was selected.

### 7. SOLID principles
- **SRP:** each class has one primary responsibility.
- **OCP:** new strategies can be added as new implementations.
- **DIP:** the planner depends on the `RouteStrategy` abstraction.

### 8. Strategy vs Bridge
Strategy encapsulates interchangeable algorithms and lets the application change behavior at runtime. Bridge separates an abstraction from its implementation so both dimensions can evolve independently. The route planner needs interchangeable algorithms, so Strategy is appropriate.

### 9. Limitations
The application uses static demonstration data and illustrative cost/emissions formulas. It is not connected to live maps, traffic feeds, fleet availability or real pricing. Balanced scores are normalized against the currently valid routes.

### 10. Testing
The JUnit 5 suite includes tests for fastest, cheapest, eco and balanced strategies; fifth strategy; runtime switching; automatic selection; budget, deadline, availability, prohibition and emission constraints; no valid route; invalid weights; comparison mode; boundary values; and invalid input. Execute `mvn test` and insert the actual test summary/screenshot before submission.

### UML diagrams
The class and sequence diagrams are included in `README.md` as Mermaid diagrams. Export them to PNG/SVG if your instructor requires image files in the report.

### Before submission
1. Run `mvn test` and save a screenshot of the successful result.
2. Run `Main` and save a screenshot of the console output.
3. Commit the project in at least 8 meaningful Git commits (do not submit everything as one commit).
4. Replace/add your name and course details if required by your teacher.
