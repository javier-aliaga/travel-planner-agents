package io.dapr.examples.travel;

import io.dapr.examples.travel.agents.WeatherAssistant;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for the weather assistant agent.
 *
 * <p>Example usage:
 * <pre>
 * curl "http://localhost:8080/weather?city=Paris"
 * </pre>
 */
@Path("/weather")
public class WeatherResource {

    @Inject
    WeatherAssistant weatherAssistant;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String checkWeather(
            @QueryParam("city") @DefaultValue("Paris") String city) {
        return weatherAssistant.checkWeather(city);
    }
}
