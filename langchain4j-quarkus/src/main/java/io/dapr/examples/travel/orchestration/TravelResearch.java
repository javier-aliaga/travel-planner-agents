package io.dapr.examples.travel.orchestration;

import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.ParallelAgent;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.agents.ActivityPlanner;
import io.dapr.examples.travel.agents.FlightFinder;
import io.dapr.examples.travel.agents.HotelFinder;

/**
 * Parallel orchestration agent that runs flight search, hotel search, and activity
 * planning concurrently, backed by a Dapr Workflow.
 *
 * <p>All three sub-agents execute in parallel via a {@code ParallelOrchestrationWorkflow}.
 * Each sub-agent's tool calls are individually tracked as durable Dapr Workflow Activities.
 *
 * <p>The {@link Output} method combines the three results into a single {@link TravelResearchResult}.
 */
public interface TravelResearch {

    @ParallelAgent(name = "travel-research-agent",
            outputKey = "research",
            subAgents = {FlightFinder.class, HotelFinder.class, ActivityPlanner.class})
    TravelResearchResult research(@V("origin") String origin,
                                  @V("destination") String destination,
                                  @V("date") String date,
                                  @V("nights") int nights,
                                  @V("interests") String interests);

    @Output
    static TravelResearchResult output(String flights, String hotels, String activities) {
        if (flights == null || hotels == null || activities == null) {
            return new TravelResearchResult("ERROR", flights, hotels, activities);
        }
        return new TravelResearchResult("OK", flights, hotels, activities);
    }
}
