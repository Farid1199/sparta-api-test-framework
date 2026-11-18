Feature: Authentication
  Scenario: Valid login returns JWT token
    When I send a POST request to "/Auth/login" with valid credentials
    Then the response status should be 200
    And a bearer token should be returned