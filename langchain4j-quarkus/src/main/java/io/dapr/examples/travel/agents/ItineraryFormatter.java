package io.dapr.examples.travel.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Agent that combines flight, hotel, and activity information into a final itinerary.
 * Does not use any tools — purely LLM-based text composition.
 */
public interface ItineraryFormatter {

    @UserMessage("""
            You are a travel itinerary formatter.
            Combine the following travel information into a well-organized itinerary:

            FLIGHTS: {{flights}}
            HOTELS: {{hotels}}
            ACTIVITIES: {{activities}}

            Create a clear, concise travel itinerary that includes:
            - Travel details (flight)
            - Accommodation details (hotel)
            - Day-by-day activity suggestions
            - Estimated total budget

            Format it in a readable way.
            """)
    @Agent(name = "itinerary-formatter-agent",
            description = "Combines travel details into a formatted itinerary",
            outputKey = "itinerary")
    String formatItinerary(@V("flights") String flights,
                           @V("hotels") String hotels,
                           @V("activities") String activities);
}
