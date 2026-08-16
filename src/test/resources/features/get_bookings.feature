@booking @get @api
Feature: Get Bookings API
  As an API consumer
  I want to retrieve bookings
  So that I can view existing reservations

  @smoke
  Scenario: Get all bookings returns list
    When I send a GET request to "/booking"
    Then the response status code should be 200
    And the response body should be a non-empty list
    And the response time should be less than 5000 ms

  Scenario: Get booking by valid ID
    Given a booking exists in the system
    When I send a GET request for the booking by its ID
    Then the response status code should be 200
    And the response should contain field "firstname"
    And the response should contain field "lastname"
    And the response should contain field "totalprice"
    And the response should match schema "booking-schema.json"

  @negative
  Scenario: Get booking with invalid ID
    When I send a GET request to "/booking/9999999"
    Then the response status code should be 404

  Scenario: Filter bookings by name
    When I filter bookings by firstname "John" and lastname "Smith"
    Then the response status code should be 200
    And the response should be a JSON array
