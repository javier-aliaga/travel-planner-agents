package io.dapr.examples.travel.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Tools that simulate slow external API calls. Used to demonstrate
 * crash recovery — the delay gives you time to kill the process.
 */
@ApplicationScoped
public class SlowApiTools {

    private static final Logger LOG = Logger.getLogger(SlowApiTools.class);

    @Tool("Fetch a detailed travel advisory for a country from the government database. "
            + "This is a slow API call that takes about 30 seconds.")
    public String fetchTravelAdvisory(@P("the country name") String country) {
        LOG.infof(">>> fetchTravelAdvisory(%s) — sleeping 30s (kill the process now to test recovery)",
                country);
        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LOG.infof(">>> fetchTravelAdvisory(%s) — woke up, returning result", country);
        return country + ": Level 1 — Exercise Normal Precautions. "
                + "No travel restrictions. Standard vaccinations recommended. "
                + "Local emergency number: 112.";
    }
}
