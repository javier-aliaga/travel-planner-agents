package io.dapr.examples.travel.orchestration;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.agents.ItineraryFormatter;

/**
 * Top-level sequential orchestration agent that first researches travel options
 * in parallel, then formats the results into a complete itinerary.
 *
 * <pre>
 * TravelPlanner (@SequenceAgent)
 *   1. TravelResearch (@ParallelAgent)
 *      - FlightFinder   (uses FlightTools)
 *      - HotelFinder    (uses HotelTools)
 *      - ActivityPlanner (uses ActivityTools)
 *   2. ItineraryFormatter (no tools, LLM-only)
 * </pre>
 *
 * <p>Demonstrates nested orchestration: a sequential workflow containing a parallel
 * workflow, all backed by Dapr Workflows for durability and observability.
 */
public interface TravelPlanner {

    @SequenceAgent(name = "travel-planner-agent",
            outputKey = "itinerary",
            subAgents = {TravelResearch.class, ItineraryFormatter.class})
    String plan(@V("origin") String origin,
                @V("destination") String destination,
                @V("date") String date,
                @V("nights") int nights,
                @V("interests") String interests);
}
