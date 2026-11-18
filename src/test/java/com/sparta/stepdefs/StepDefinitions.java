package com.sparta.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    private Response response;
    private static String token;
    private final String BASE_URL = "http://localhost:8080";

    // --- Authentication Steps ---
    @When("I send a POST request to {string} with valid credentials")
    public void sendPostLogin(String endpoint) {
        String loginBody = "{\"username\": \"sparta\", \"password\": \"global\"}";

        response = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(loginBody)
                .when()
                .post(endpoint);
    }

    @And("a bearer token should be returned")
    public void bearerTokenShouldBeReturned() {
        token = response.jsonPath().getString("token");
        assertNotNull(token, "Expected a JWT token but got null");
        assertFalse(token.trim().isEmpty(), "JWT token should not be empty");
    }

    // --- Access Control Steps ---
    @When("I send a GET request to {string} without a token")
    public void sendGetWithoutToken(String endpoint) {
        response = given()
                .baseUri(BASE_URL)
                .when()
                .get(endpoint);
    }

    // --- Data Validation Steps ---
    @When("I create a spartan with a first name shorter than 6 characters")
    public void createSpartanShortFirstName() {
        // Authenticate first if token is missing
        if (token == null) {
            sendPostLogin("/Auth/login");
            token = response.jsonPath().getString("token");
        }

        // "Bob" is only 3 letters, which triggers the model validation constraint (< 6 characters)
        String invalidPayload = "{\n" +
                "  \"firstName\": \"Bob\",\n" +
                "  \"lastName\": \"Smithson\",\n" +
                "  \"university\": \"University of Oxford\",\n" +
                "  \"degree\": \"Computer Science\",\n" +
                "  \"courseId\": 1,\n" +
                "  \"graduated\": true\n" +
                "}";

        response = given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(invalidPayload)
                .when()
                .post("/api/spartans");
    }

    // --- Shared Response Verification Step ---
    @Then("the response status should be {int}")
    public void verifyStatus(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @When("I submit spartans loaded from external file {string}")
    public void submitSpartansFromExternalFile(String filePath) throws Exception {
        if (token == null) {
            sendPostLogin("/Auth/login");
            token = response.jsonPath().getString("token");
        }

        com.sparta.utils.JsonDataReader reader = new com.sparta.utils.JsonDataReader();
        String jsonContent = reader.readJsonFile(filePath);

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(jsonContent);

        for (com.fasterxml.jackson.databind.JsonNode node : rootNode) {
            response = given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .body(node.toString())
                    .when()
                    .post("/api/spartans");

            response.prettyPrint();

            assertEquals(201, response.getStatusCode(), "Failed to create spartan: " + node.toString());
        }
    }

    @Then("the data-driven spartans should be created with status {int}")
    public void verifyDataDrivenSpartansStatus(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.getStatusCode());
    }
}