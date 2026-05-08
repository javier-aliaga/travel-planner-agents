package io.dapr.examples.travel.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.tools.HotelTools;
import io.quarkiverse.langchain4j.ToolBox;

/**
 * Agent that finds hotels in a destination city using the {@link HotelTools}.
 */
public interface HotelFinder {

    @ToolBox(HotelTools.class)
    @UserMessage("""
            You are a hotel search assistant.
            Find hotels in {{destination}} with check-in on {{date}} for {{nights}} nights.
            Use the search tool to get hotel options, then recommend the best option
            considering price, location, and rating.
            Return a concise summary of the recommended hotel.
            """)
    @Agent(name = "hotel-finder-agent",
            description = "Searches and recommends hotels in a city",
            outputKey = "hotels")
    String findHotels(@V("destination") String destination,
                      @V("date") String date,
                      @V("nights") int nights);
}
