package com.api.endpoints;

import com.api.models.Booking;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Booking endpoint - CRUD operations on bookings.
 */
public class BookingEndpoint extends BaseEndpoint {

    private static final String BOOKINGS = "/booking";
    private static final String BOOKING_BY_ID = "/booking/{id}";
    private static final String PING = "/ping";

    /** GET /booking - returns all booking IDs */
    public static Response getAllBookings() {
        logger.info("GET /booking - Fetching all bookings");
        return given().spec(getRequestSpec())
                .when().get(BOOKINGS);
    }

    /** GET /booking?firstname=X&lastname=Y - filter bookings */
    public static Response getBookingsByName(String firstname, String lastname) {
        logger.info("GET /booking - Filter by name: {} {}", firstname, lastname);
        return given().spec(getRequestSpec())
                .queryParam("firstname", firstname)
                .queryParam("lastname", lastname)
                .when().get(BOOKINGS);
    }

    /** GET /booking/{id} - get specific booking */
    public static Response getBookingById(int id) {
        logger.info("GET /booking/{} - Fetching booking by ID", id);
        return given().spec(getRequestSpec())
                .pathParam("id", id)
                .when().get(BOOKING_BY_ID);
    }

    /** POST /booking - create new booking */
    public static Response createBooking(Booking booking) {
        logger.info("POST /booking - Creating new booking for: {} {}",
                booking.getFirstname(), booking.getLastname());
        return given().spec(getRequestSpec())
                .body(booking)
                .when().post(BOOKINGS);
    }

    /** PUT /booking/{id} - update booking (full update, requires auth) */
    public static Response updateBooking(int id, Booking booking, String token) {
        logger.info("PUT /booking/{} - Updating booking", id);
        return given().spec(getAuthSpec(token))
                .pathParam("id", id)
                .body(booking)
                .when().put(BOOKING_BY_ID);
    }

    /** PATCH /booking/{id} - partial update (requires auth) */
    public static Response partialUpdateBooking(int id, Booking booking, String token) {
        logger.info("PATCH /booking/{} - Partial update", id);
        return given().spec(getAuthSpec(token))
                .pathParam("id", id)
                .body(booking)
                .when().patch(BOOKING_BY_ID);
    }

    /** DELETE /booking/{id} - delete booking (requires auth) */
    public static Response deleteBooking(int id, String token) {
        logger.info("DELETE /booking/{} - Deleting booking", id);
        return given().spec(getAuthSpec(token))
                .pathParam("id", id)
                .when().delete(BOOKING_BY_ID);
    }

    /** GET /ping - health check */
    public static Response ping() {
        logger.info("GET /ping - Health check");
        return given().spec(getRequestSpec()).when().get(PING);
    }
}
