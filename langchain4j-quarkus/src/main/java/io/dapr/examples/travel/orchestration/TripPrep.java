package io.dapr.examples.travel.orchestration;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.agents.CityGuide;
import io.dapr.examples.travel.agents.WeatherAssistant;

/**
 * Sequential orchestration: check weather first, then create a city guide.
 */
public interface TripPrep {

    @SequenceAgent(name = "trip-prep",
            outputKey = "guide",
            subAgents = {WeatherAssistant.class, CityGuide.class})
    String prepare(@V("city") String city,
                   @V("cuisine") String cuisine,
                   @V("days") int days);
}
