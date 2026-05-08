package io.dapr.examples.travel;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Integration test for the travel planning workflow.
 * Requires Docker for Dapr dev services.
 * Uses {@link MockChatModel} instead of a real LLM.
 */
@QuarkusTest
@ExtendWith(DockerAvailableCondition.class)
class TravelResourceTest {

    @Test
    void planTripReturnsResponse() {
        given()
                .queryParam("origin", "New York")
                .queryParam("destination", "Paris")
                .queryParam("date", "2025-07-01")
                .queryParam("nights", 5)
                .queryParam("interests", "history, food")
                .when()
                .get("/travel/plan")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    void planTripWithDefaultParams() {
        given()
                .when()
                .get("/travel/plan")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    void planTripResponseContainsContent() {
        String body = given()
                .queryParam("origin", "London")
                .queryParam("destination", "Tokyo")
                .queryParam("date", "2025-09-01")
                .queryParam("nights", 7)
                .queryParam("interests", "food, technology, anime")
                .when()
                .get("/travel/plan")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assert !body.isBlank() : "Travel plan response should not be blank";
    }
}
