package io.dapr.examples.travel;

import io.dapr.examples.travel.agents.TravelAdvisor;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

/**
 * Endpoint for testing crash recovery. The TravelAdvisor agent uses a
 * tool that sleeps for 30 seconds, giving you time to kill the process.
 *
 * <pre>
 * # 1. Start the app and trigger the slow agent
 * curl "http://localhost:8080/crash-test?country=France"
 *
 * # 2. While "sleeping 30s" appears in logs, kill the process:
 * kill -9 $(lsof -ti :8080)
 *
 * # 3. Restart the app — watch logs for:
 * #    "Recovery timeout — no AiServices thread detected"
 * #    "Starting recovery for agent=travel-advisor"
 * #    "Recovery complete"
 * </pre>
 */
@Path("/crash-test")
public class CrashRecoveryResource {

    private static final Logger LOG = Logger.getLogger(CrashRecoveryResource.class);

    @Inject
    TravelAdvisor travelAdvisor;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String crashTest(@QueryParam("country") String country) {
        if (country == null || country.isBlank()) {
            country = "France";
        }
        LOG.infof("Starting crash recovery test for country=%s", country);
        String result = travelAdvisor.getAdvisory(country);
        LOG.infof("Crash recovery test complete: %s", result);
        return result;
    }
}
