package io.dapr.examples.travel.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.tools.ActivityTools;
import io.quarkiverse.langchain4j.ToolBox;

/**
 * Agent that plans activities and attractions using the {@link ActivityTools}.
 */
public interface ActivityPlanner {

    @ToolBox(ActivityTools.class)
    @UserMessage("""
            You are a travel activities planner.
            Find the best activities and attractions in {{destination}}
            matching the traveler's interests: {{interests}}.
            Use the search tool to get options, then select the top 3
            that best match the interests.
            Return a concise list of recommended activities with brief descriptions.
            """)
    @Agent(name = "activity-planner-agent",
            description = "Plans activities and attractions based on interests",
            outputKey = "activities")
    String planActivities(@V("destination") String destination,
                          @V("interests") String interests);
}
