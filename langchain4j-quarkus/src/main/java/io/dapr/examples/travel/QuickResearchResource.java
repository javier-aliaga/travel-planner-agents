package io.dapr.examples.travel;

import io.dapr.examples.travel.orchestration.QuickResearch;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for parallel quick research.
 *
 * <p>Example usage:
 * <pre>
 * curl "http://localhost:8080/research?city=Paris&amp;cuisine=french&amp;days=3"
 * </pre>
 */
@Path("/research")
public class QuickResearchResource {

    @Inject
    QuickResearch quickResearch;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String research(
            @QueryParam("city") @DefaultValue("Paris") String city,
            @QueryParam("cuisine") @DefaultValue("any") String cuisine,
            @QueryParam("days") @DefaultValue("3") int days) {
        return quickResearch.research(city, cuisine, days);
    }
}
