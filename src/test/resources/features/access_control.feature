Feature: Access Control
  Scenario: Reject unauthenticated requests
    When I send a GET request to "/api/spartans" without a token
    Then the response status should be 401