@booking @put @patch @api
Feature: Update Booking API
  As an authenticated API consumer
  I want to update existing bookings
  So that I can modify reservation details

  Background:
    Given I have a valid authentication token
    And I have created a new booking

  @smoke @positive
  Scenario: Full update booking with PUT
    Given I update the booking with:
      | firstname       | Updated        |
      | lastname        | Name           |
      | totalprice      | 999            |
      | depositpaid     | false          |
      | checkin         | 2026-07-01     |
      | checkout        | 2026-07-15     |
      | additionalneeds | Spa Service    |
    When I send a PUT request for the created booking
    Then the response status code should be 200
    And the response should contain firstname "Updated"
    And the response should contain totalprice 999

  @smoke @positive
  Scenario: Partial update booking with PATCH
    When I send a PATCH request to update firstname to "PartialUpdate"
    Then the response status code should be 200
    And the response should contain firstname "PartialUpdate"

  @negative @auth
  Scenario: Update booking without auth token returns 403
    Given I have an invalid authentication token "invalid-token-123"
    When I send a PUT request for the created booking
    Then the response status code should be 403

  @negative
  Scenario: Update non-existent booking returns 405
    When I send a PUT request to "/booking/9999999" with valid token
    Then the response status code should be 405
