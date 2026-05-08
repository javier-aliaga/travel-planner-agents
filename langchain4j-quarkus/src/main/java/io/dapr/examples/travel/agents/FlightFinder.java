package io.dapr.examples.travel.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.tools.FlightTools;
import io.quarkiverse.langchain4j.ToolBox;

/**
 * Agent that finds flights between two cities using the {@link FlightTools}.
 */
public interface FlightFinder {

    @ToolBox(FlightTools.class)
    @UserMessage("""
            You are a flight search assistant.
            Find the best available flights from {{origin}} to {{destination}} on {{date}}.
            Use the search tool to get flight options, then recommend the best option
            considering price and convenience.
            Return a concise summary of the recommended flight.
            """)
    @Agent(name = "flight-finder-agent",
            description = "Searches and recommends flights between cities",
            outputKey = "flights")
    String findFlights(@V("origin") String origin,
                       @V("destination") String destination,
                       @V("date") String date);
}
