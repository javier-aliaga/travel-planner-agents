package io.dapr.examples.travel;

import io.dapr.examples.travel.orchestration.ItineraryRefiner;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for the loop-based itinerary refiner.
 *
 * <p>Example usage:
 * <pre>
 * curl "http://localhost:8080/refine?city=Paris&amp;cuisine=french&amp;days=3"
 * </pre>
 */
@Path("/refine")
public class ItineraryRefinerResource {

    @Inject
    ItineraryRefiner itineraryRefiner;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String refine(
            @QueryParam("city") @DefaultValue("Paris") String city,
            @QueryParam("cuisine") @DefaultValue("any") String cuisine,
            @QueryParam("days") @DefaultValue("3") int days) {
        return itineraryRefiner.refine(city, cuisine, days);
    }
}
