package io.dapr.examples.travel.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.tools.SlowApiTools;
import io.quarkiverse.langchain4j.ToolBox;

/**
 * Agent that fetches travel advisories using a slow external API.
 * Used to demonstrate crash recovery — the slow tool gives time to kill the process.
 */
public interface TravelAdvisor {

    @ToolBox(SlowApiTools.class)
    @UserMessage("""
            You are a travel safety advisor.
            Fetch the travel advisory for {{country}} and summarize the key points:
            safety level, any restrictions, and health recommendations.
            Keep it concise (2-3 sentences).
            """)
    @Agent(name = "travel-advisor",
            description = "Fetches and summarizes travel advisories for a country",
            outputKey = "advisory")
    String getAdvisory(@V("country") String country);
}
