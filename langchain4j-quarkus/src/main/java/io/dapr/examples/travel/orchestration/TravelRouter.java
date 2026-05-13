package io.dapr.examples.travel.orchestration;

import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.agents.CityGuide;
import io.dapr.examples.travel.agents.WeatherAssistant;

/**
 * Conditional orchestration: routes to different agents based on trip duration.
 * Quick trips (1 day or less) get just the weather check.
 * Longer trips get the full city guide.
 */
public interface TravelRouter {

    @ConditionalAgent(name = "travel-router",
            outputKey = "recommendation",
            subAgents = {WeatherAssistant.class, CityGuide.class})
    String route(@V("city") String city,
                 @V("cuisine") String cuisine,
                 @V("days") int days);

    @ActivationCondition(WeatherAssistant.class)
    static boolean quickTrip(@V("days") int days) {
        return days <= 1;
    }

    @ActivationCondition(CityGuide.class)
    static boolean longTrip(@V("days") int days) {
        return days > 1;
    }
}
