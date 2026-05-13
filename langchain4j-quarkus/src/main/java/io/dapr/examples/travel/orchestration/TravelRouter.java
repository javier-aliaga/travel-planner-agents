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
 *
 * Both agents write to the same outputKey so the result is always available.
 */
public interface TravelRouter {

    @ConditionalAgent(name = "travel-router",
            outputKey = "weather",
            subAgents = {WeatherAssistant.class, CityGuide.class})
    String route(@V("city") String city,
                 @V("cuisine") String cuisine,
                 @V("days") int days);

    @ActivationCondition(WeatherAssistant.class)
    static boolean activateWeather(@V("days") int days) {
        // Always check weather
        return true;
    }

    @ActivationCondition(CityGuide.class)
    static boolean activateGuide(@V("days") int days) {
        // Full guide only for longer trips
        return days > 1;
    }
}
