package io.dapr.examples.travel;

import io.dapr.examples.travel.agents.CityGuide;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for the city guide agent.
 *
 * <p>Example usage:
 * <pre>
 * curl "http://localhost:8080/guide?city=Paris&amp;cuisine=french&amp;days=3"
 * </pre>
 */
@Path("/guide")
public class CityGuideResource {

    @Inject
    CityGuide cityGuide;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String createGuide(
            @QueryParam("city") @DefaultValue("Paris") String city,
            @QueryParam("cuisine") @DefaultValue("any") String cuisine,
            @QueryParam("days") @DefaultValue("3") int days) {
        return cityGuide.createGuide(city, cuisine, days);
    }
}
