package io.dapr.examples.travel.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.tools.WeatherTools;
import io.quarkiverse.langchain4j.ToolBox;

/**
 * Simple agent that checks the weather for a given city.
 */
public interface WeatherAssistant {

    @ToolBox(WeatherTools.class)
    @UserMessage("""
            You are a weather assistant.
            Check the current weather in {{city}} and provide a brief summary
            including temperature, conditions, and what to wear.
            """)
    @Agent(name = "weather-assistant",
            description = "Checks weather conditions for a city",
            outputKey = "weather")
    String checkWeather(@V("city") String city);
}
