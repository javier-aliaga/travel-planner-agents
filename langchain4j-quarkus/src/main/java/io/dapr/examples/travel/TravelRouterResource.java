package io.dapr.examples.travel;

import io.dapr.examples.travel.orchestration.TravelRouter;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for the conditional travel router.
 *
 * <p>Example usage:
 * <pre>
 * curl "http://localhost:8080/route?city=Paris&amp;days=1"    # quick trip → weather only
 * curl "http://localhost:8080/route?city=Paris&amp;days=3"    # long trip → full city guide
 * </pre>
 */
@Path("/route")
public class TravelRouterResource {

    @Inject
    TravelRouter travelRouter;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String route(
            @QueryParam("city") @DefaultValue("Paris") String city,
            @QueryParam("cuisine") @DefaultValue("any") String cuisine,
            @QueryParam("days") @DefaultValue("3") int days) {
        return travelRouter.route(city, cuisine, days);
    }
}
