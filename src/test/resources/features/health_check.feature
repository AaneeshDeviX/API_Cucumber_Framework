@health @api
Feature: API Health Check
  As an API consumer
  I want to verify the API is up
  So that I can rely on it for testing

  @smoke
  Scenario: Health check returns 201
    When I send a GET request to "/ping"
    Then the response status code should be 201
    And the response time should be less than 3000 ms
