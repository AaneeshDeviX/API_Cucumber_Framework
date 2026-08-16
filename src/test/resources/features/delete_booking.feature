@booking @delete @api
Feature: Delete Booking API
  As an authenticated API consumer
  I want to delete bookings
  So that I can remove cancelled reservations

  Background:
    Given I have a valid authentication token

  @smoke @positive
  Scenario: Delete an existing booking
    Given I have created a new booking
    When I send a DELETE request for the created booking
    Then the response status code should be 201
    And the booking should no longer exist when I GET it

  @negative @auth
  Scenario: Delete without authentication returns 403
    Given I have created a new booking
    And I have an invalid authentication token "invalid"
    When I send a DELETE request for the created booking
    Then the response status code should be 403

  @negative
  Scenario: Delete non-existent booking
    When I send a DELETE request to "/booking/9999999" with valid token
    Then the response status code should be 405
