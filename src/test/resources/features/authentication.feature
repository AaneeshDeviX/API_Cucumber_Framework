@auth @api
Feature: Authentication API
  As an API consumer
  I want to obtain an access token
  So that I can perform authenticated operations

  @smoke @positive
  Scenario: Generate token with valid credentials
    Given I have valid admin credentials
    When I send a POST request to "/auth"
    Then the response status code should be 200
    And the response should contain a valid token
    And the response should match schema "auth-schema.json"

  @negative
  Scenario: Token generation with invalid username
    Given I have invalid username "wronguser" and password "password123"
    When I send a POST request to "/auth"
    Then the response status code should be 200
    And the response should contain reason "Bad credentials"

  @negative
  Scenario: Token generation with invalid password
    Given I have invalid username "admin" and password "wrongpass"
    When I send a POST request to "/auth"
    Then the response status code should be 200
    And the response should contain reason "Bad credentials"

  @negative
  Scenario Outline: Authentication with various invalid inputs
    Given I have invalid username "<username>" and password "<password>"
    When I send a POST request to "/auth"
    Then the response should contain reason "Bad credentials"

    Examples:
      | username  | password    |
      | wrong     | password123 |
      | admin     | wrong       |
      | wrong     | wrong       |
