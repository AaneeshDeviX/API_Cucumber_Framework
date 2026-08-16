@e2e @integration @api
Feature: End-to-End Booking Lifecycle Integration
  As a tester
  I want to verify the full booking lifecycle
  So that I can validate API integration across operations

  @smoke @e2e
  Scenario: Full CRUD lifecycle - Create, Read, Update, Delete
    # Step 1: Authenticate
    Given I have valid admin credentials
    When I send a POST request to "/auth"
    Then the response status code should be 200
    And I save the token from response

    # Step 2: Create a new booking
    Given I have a valid booking payload
    When I send a POST request to "/booking"
    Then the response status code should be 200
    And I save the booking ID from response

    # Step 3: Verify the booking exists
    When I send a GET request for the booking by its ID
    Then the response status code should be 200
    And the response should contain field "firstname"

    # Step 4: Update the booking
    When I send a PATCH request to update firstname to "Integration"
    Then the response status code should be 200
    And the response should contain firstname "Integration"

    # Step 5: Verify update persisted
    When I send a GET request for the booking by its ID
    Then the response status code should be 200
    And the response should contain firstname "Integration"

    # Step 6: Delete the booking
    When I send a DELETE request for the created booking
    Then the response status code should be 201

    # Step 7: Verify deletion
    When I send a GET request for the booking by its ID
    Then the response status code should be 404
