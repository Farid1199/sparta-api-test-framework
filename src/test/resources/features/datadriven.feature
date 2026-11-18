Feature: Data-Driven Spartan Creation
  Scenario: Create multiple spartans using external JSON data
    When I submit spartans loaded from external file "testdata/spartans.json"
    Then the data-driven spartans should be created with status 201