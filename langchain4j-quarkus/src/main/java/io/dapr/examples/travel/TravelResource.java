package io.dapr.examples.travel;

import io.dapr.examples.travel.orchestration.TravelPlanner;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint that triggers the travel planning workflow.
 *
 * <p>Example usage:
 * <pre>
 * curl "http://localhost:8080/travel/plan?origin=NYC&amp;destination=Paris&amp;date=2025-06-15&amp;nights=5&amp;interests=history,food"
 * </pre>
 */
@Path("/travel")
public class TravelResource {

    @Inject
    TravelPlanner travelPlanner;

    @GET
    @Path("/plan")
    @Produces(MediaType.TEXT_PLAIN)
    public String planTrip(
            @QueryParam("origin") @DefaultValue("New York") String origin,
            @QueryParam("destination") @DefaultValue("Paris") String destination,
            @QueryParam("date") @DefaultValue("2025-07-01") String date,
            @QueryParam("nights") @DefaultValue("5") int nights,
            @QueryParam("interests") @DefaultValue("history, food, culture") String interests) {
        return travelPlanner.plan(origin, destination, date, nights, interests);
    }
}
