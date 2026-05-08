package io.dapr.examples.travel.orchestration;

import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.ParallelAgent;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.agents.CityGuide;
import io.dapr.examples.travel.agents.WeatherAssistant;

/**
 * Parallel orchestration: check weather AND create city guide at the same time.
 */
public interface QuickResearch {

    @ParallelAgent(name = "quick-research",
            outputKey = "quick-research-result",
            subAgents = {WeatherAssistant.class, CityGuide.class})
    String research(@V("city") String city,
                    @V("cuisine") String cuisine,
                    @V("days") int days);

    @Output
    static String output(String weather, String guide) {
        return "=== WEATHER ===\n" + weather + "\n\n=== CITY GUIDE ===\n" + guide;
    }
}
