Feature: Data Validation
  Scenario: First name under 6 characters fails
    When I create a spartan with a first name shorter than 6 characters
    Then the response status should be 400
