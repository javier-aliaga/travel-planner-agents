package io.dapr.examples.travel.orchestration;

import dev.langchain4j.agentic.declarative.LoopAgent;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.agents.CityGuide;
import io.dapr.examples.travel.agents.WeatherAssistant;

/**
 * Loop orchestration: runs WeatherAssistant and CityGuide in a loop
 * (max 2 iterations) to accumulate and refine travel information.
 */
public interface ItineraryRefiner {

    @LoopAgent(name = "itinerary-refiner",
            outputKey = "refined-itinerary",
            maxIterations = 2,
            subAgents = {WeatherAssistant.class, CityGuide.class})
    String refine(@V("city") String city,
                  @V("cuisine") String cuisine,
                  @V("days") int days);
}
