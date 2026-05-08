package io.dapr.examples.travel.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mock weather tools. In production, these would call a real weather API.
 */
@ApplicationScoped
public class WeatherTools {

    @Tool("Get the current weather for a city")
    public String getWeather(@P("the city name") String city) {
        return switch (city.toLowerCase()) {
            case "paris" -> "Paris: 22C, partly cloudy, light breeze. Pleasant for walking.";
            case "tokyo" -> "Tokyo: 28C, humid, chance of afternoon rain. Carry an umbrella.";
            case "rome" -> "Rome: 30C, sunny and hot. Stay hydrated, wear sunscreen.";
            case "new york", "nyc" -> "New York: 18C, clear skies, cool evening expected.";
            case "london" -> "London: 15C, overcast with light drizzle. Bring a jacket.";
            default -> city + ": 20C, clear skies, comfortable conditions.";
        };
    }
}
