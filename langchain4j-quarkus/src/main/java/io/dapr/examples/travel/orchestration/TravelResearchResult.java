package io.dapr.examples.travel.orchestration;

/**
 * Result of the parallel travel research phase.
 */
public record TravelResearchResult(String status, String flights, String hotels, String activities) {
}
