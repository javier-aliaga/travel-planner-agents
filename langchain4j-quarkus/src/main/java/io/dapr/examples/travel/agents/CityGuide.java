package io.dapr.examples.travel.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.dapr.examples.travel.tools.CityGuideTools;
import io.quarkiverse.langchain4j.ToolBox;

/**
 * Agent that provides a city travel guide with restaurants, attractions,
 * and transport info. Uses multiple tools and multi-step reasoning.
 */
public interface CityGuide {

    @ToolBox(CityGuideTools.class)
    @SystemMessage("""
            You are an expert city travel guide. When asked about a city, you MUST:
            1. First find the top attractions using the findAttractions tool
            2. Then search for restaurants matching the user's cuisine preference using searchRestaurants
            3. Finally get transport information using getTransportInfo
            Combine all results into a well-organized city guide with sections for
            Attractions, Restaurants, and Getting Around.
            Keep it concise but informative.
            """)
    @UserMessage("""
            Create a city guide for {{city}}.
            Cuisine preference: {{cuisine}}.
            Trip duration: {{days}} days.
            """)
    @Agent(name = "city-guide",
            description = "Creates comprehensive city guides with attractions, restaurants, and transport info",
            outputKey = "guide")
    String createGuide(@V("city") String city,
                       @V("cuisine") String cuisine,
                       @V("days") int days);
}
