@booking @post @api
Feature: Create Booking API
  As an API consumer
  I want to create new bookings
  So that I can make reservations

  @smoke @positive
  Scenario: Create booking with valid data
    Given I have a valid booking payload
    When I send a POST request to "/booking"
    Then the response status code should be 200
    And the response should contain a "bookingid"
    And the response should match schema "create-booking-schema.json"
    And the booking details should match the request

  @positive
  Scenario: Create booking with all fields
    Given I create a booking with:
      | firstname       | Jane           |
      | lastname        | Wilson         |
      | totalprice      | 500            |
      | depositpaid     | true           |
      | checkin         | 2026-06-01     |
      | checkout        | 2026-06-10     |
      | additionalneeds | Late Checkin   |
    When I send a POST request to "/booking"
    Then the response status code should be 200
    And the response should contain firstname "Jane"
    And the response should contain lastname "Wilson"
    And the response should contain totalprice 500

  @negative
  Scenario: Create booking with empty payload
    Given I have an empty booking payload
    When I send a POST request to "/booking"
    Then the response status code should be 500

  # KNOWN DEFECT — Restful-Booker accepts a non-numeric totalprice ("abc") and
  # returns 200 with a created booking instead of rejecting the payload. The
  # assertion below states the CORRECT expected behaviour, so this scenario fails
  # by design and documents the defect. It is excluded from the CI gate via
  # `not @known-defect` so the badge tracks regressions rather than this bug.
  @negative @known-defect
  Scenario: Create booking with invalid data type is rejected
    Given I have a booking with invalid totalprice "abc"
    When I send a POST request to "/booking"
    Then the response status code should be 500
