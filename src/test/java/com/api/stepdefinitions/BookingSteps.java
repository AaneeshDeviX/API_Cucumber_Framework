package com.api.stepdefinitions;

import com.api.endpoints.AuthEndpoint;
import com.api.endpoints.BookingEndpoint;
import com.api.models.AuthRequest;
import com.api.models.Booking;
import com.api.models.BookingDates;
import com.api.utils.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingSteps {

    private static final Logger logger = LogManager.getLogger(BookingSteps.class);
    private final TestContext context;
    private Booking bookingPayload;

    public BookingSteps(TestContext context) {
        this.context = context;
    }

    // ═══════════════════════════════════════
    // GIVEN - Setup steps
    // ═══════════════════════════════════════

    @Given("I have a valid booking payload")
    public void iHaveValidBookingPayload() {
        bookingPayload = Booking.createSampleBooking();
        context.setBooking(bookingPayload);
        logger.info("Valid booking payload prepared: {} {}",
                bookingPayload.getFirstname(), bookingPayload.getLastname());
    }

    @Given("I have an empty booking payload")
    public void iHaveEmptyBookingPayload() {
        bookingPayload = new Booking();
        context.setBooking(bookingPayload);
    }

    @Given("I have a booking with invalid totalprice {string}")
    public void iHaveBookingWithInvalidPrice(String invalidPrice) {
        // Create booking with invalid data type for totalprice
        bookingPayload = Booking.createSampleBooking();
        // We'll send raw map with invalid type
        Map<String, Object> rawPayload = new HashMap<>();
        rawPayload.put("firstname", "Test");
        rawPayload.put("lastname", "User");
        rawPayload.put("totalprice", invalidPrice); // string instead of int
        rawPayload.put("depositpaid", true);
        Map<String, String> dates = new HashMap<>();
        dates.put("checkin", "2026-05-01");
        dates.put("checkout", "2026-05-07");
        rawPayload.put("bookingdates", dates);
        context.set("rawPayload", rawPayload);
    }

    @Given("I create a booking with:")
    public void iCreateBookingWithDataTable(DataTable table) {
        Map<String, String> data = table.asMap(String.class, String.class);
        bookingPayload = new Booking(
                data.get("firstname"),
                data.get("lastname"),
                Integer.parseInt(data.get("totalprice")),
                Boolean.parseBoolean(data.get("depositpaid")),
                new BookingDates(data.get("checkin"), data.get("checkout")),
                data.get("additionalneeds")
        );
        context.setBooking(bookingPayload);
    }

    @Given("a booking exists in the system")
    public void aBookingExistsInSystem() {
        Response listResponse = BookingEndpoint.getAllBookings();
        Assert.assertEquals(listResponse.getStatusCode(), 200);
        List<Map<String, Integer>> bookings = listResponse.jsonPath().getList("$");
        Assert.assertFalse(bookings.isEmpty(), "There should be at least one booking");
        Integer firstId = bookings.get(0).get("bookingid");
        context.setBookingId(firstId);
        logger.info("Existing booking ID: {}", firstId);
    }

    @Given("I have created a new booking")
    public void iHaveCreatedNewBooking() {
        Booking booking = Booking.createSampleBooking();
        Response response = BookingEndpoint.createBooking(booking);
        Assert.assertEquals(response.getStatusCode(), 200);
        Integer bookingId = response.jsonPath().getInt("bookingid");
        context.setBookingId(bookingId);
        context.setBooking(booking);
        logger.info("Background: Created booking with ID {}", bookingId);
    }

    @Given("I update the booking with:")
    public void iUpdateBookingWith(DataTable table) {
        Map<String, String> data = table.asMap(String.class, String.class);
        bookingPayload = new Booking(
                data.get("firstname"),
                data.get("lastname"),
                Integer.parseInt(data.get("totalprice")),
                Boolean.parseBoolean(data.get("depositpaid")),
                new BookingDates(data.get("checkin"), data.get("checkout")),
                data.get("additionalneeds")
        );
        context.setBooking(bookingPayload);
    }

    // ═══════════════════════════════════════
    // WHEN - Execute API requests
    // ═══════════════════════════════════════

    @When("I send a GET request to {string}")
    public void iSendGetRequest(String path) {
        Response response;
        if (path.equals("/ping")) {
            response = BookingEndpoint.ping();
        } else if (path.equals("/booking")) {
            response = BookingEndpoint.getAllBookings();
        } else if (path.startsWith("/booking/")) {
            int id = Integer.parseInt(path.substring("/booking/".length()));
            response = BookingEndpoint.getBookingById(id);
        } else {
            throw new RuntimeException("Unsupported GET path: " + path);
        }
        context.setResponse(response);
    }

    @When("I send a POST request to {string}")
    public void iSendPostRequest(String path) {
        Response response;
        if (path.equals("/auth")) {
            AuthRequest req = (AuthRequest) context.get("authRequest");
            response = AuthEndpoint.createToken(req);
        } else if (path.equals("/booking")) {
            Map<String, Object> raw = (Map<String, Object>) context.get("rawPayload");
            if (raw != null) {
                response = io.restassured.RestAssured
                        .given().spec(com.api.endpoints.BaseEndpoint.getRequestSpec())
                        .body(raw)
                        .when().post("/booking");
            } else {
                response = BookingEndpoint.createBooking(context.getBooking());
            }
        } else {
            throw new RuntimeException("Unsupported POST path: " + path);
        }
        context.setResponse(response);
    }

    @When("I send a GET request for the booking by its ID")
    public void iSendGetForBookingById() {
        Response response = BookingEndpoint.getBookingById(context.getBookingId());
        context.setResponse(response);
    }

    @When("I send a PUT request for the created booking")
    public void iSendPutForCreatedBooking() {
        Response response = BookingEndpoint.updateBooking(
                context.getBookingId(),
                context.getBooking(),
                context.getToken()
        );
        context.setResponse(response);
    }

    @When("I send a PATCH request to update firstname to {string}")
    public void iSendPatchToUpdateFirstName(String newFirstname) {
        Booking partial = new Booking();
        partial.setFirstname(newFirstname);
        Response response = BookingEndpoint.partialUpdateBooking(
                context.getBookingId(),
                partial,
                context.getToken()
        );
        context.setResponse(response);
    }

    @When("I send a DELETE request for the created booking")
    public void iSendDeleteForCreatedBooking() {
        Response response = BookingEndpoint.deleteBooking(
                context.getBookingId(),
                context.getToken()
        );
        context.setResponse(response);
    }

    @When("I send a PUT request to {string} with valid token")
    public void iSendPutToPathWithValidToken(String path) {
        int id = Integer.parseInt(path.substring("/booking/".length()));
        Response response = BookingEndpoint.updateBooking(
                id, Booking.createSampleBooking(), context.getToken()
        );
        context.setResponse(response);
    }

    @When("I send a DELETE request to {string} with valid token")
    public void iSendDeleteToPathWithValidToken(String path) {
        int id = Integer.parseInt(path.substring("/booking/".length()));
        Response response = BookingEndpoint.deleteBooking(id, context.getToken());
        context.setResponse(response);
    }

    @When("I filter bookings by firstname {string} and lastname {string}")
    public void iFilterByName(String firstname, String lastname) {
        Response response = BookingEndpoint.getBookingsByName(firstname, lastname);
        context.setResponse(response);
    }

    @When("I save the booking ID from response")
    public void iSaveBookingIdFromResponse() {
        Response response = context.getResponse();
        Integer id = response.jsonPath().getInt("bookingid");
        Assert.assertNotNull(id);
        context.setBookingId(id);
        logger.info("Saved booking ID: {}", id);
    }

    // ═══════════════════════════════════════
    // THEN - Validations
    // ═══════════════════════════════════════

    @Then("the response status code should be {int}")
    public void responseStatusCodeShouldBe(int expectedCode) {
        int actualCode = context.getResponse().getStatusCode();
        Assert.assertEquals(actualCode, expectedCode,
                "Status code mismatch. Body: " + context.getResponse().getBody().asString());
    }

    @Then("the response time should be less than {int} ms")
    public void responseTimeShouldBeLessThan(int maxTime) {
        long actualTime = context.getResponse().getTime();
        logger.info("Response time: {} ms (limit: {} ms)", actualTime, maxTime);
        Assert.assertTrue(actualTime < maxTime,
                "Response time " + actualTime + " exceeded " + maxTime);
    }

    @Then("the response body should be a non-empty list")
    public void responseBodyShouldBeNonEmptyList() {
        List<Object> list = context.getResponse().jsonPath().getList("$");
        Assert.assertNotNull(list, "Response should be a list");
        Assert.assertFalse(list.isEmpty(), "List should not be empty");
        logger.info("Found {} items in response", list.size());
    }

    @Then("the response should be a JSON array")
    public void responseShouldBeJsonArray() {
        String body = context.getResponse().getBody().asString();
        Assert.assertTrue(body.startsWith("["), "Response should start with [");
    }

    @Then("the response should contain field {string}")
    public void responseShouldContainField(String field) {
        Object value = context.getResponse().jsonPath().get(field);
        Assert.assertNotNull(value, "Field '" + field + "' should be present");
    }

    @Then("the response should contain a {string}")
    public void responseShouldContainA(String key) {
        Object value = context.getResponse().jsonPath().get(key);
        Assert.assertNotNull(value, "Field '" + key + "' should be present");
    }

    @Then("the response should contain firstname {string}")
    public void responseShouldContainFirstname(String firstname) {
        // For both flat (PATCH/PUT response) and nested (POST response with booking wrapper)
        Response response = context.getResponse();
        String actual = response.jsonPath().getString("firstname");
        if (actual == null) actual = response.jsonPath().getString("booking.firstname");
        Assert.assertEquals(actual, firstname);
    }

    @Then("the response should contain lastname {string}")
    public void responseShouldContainLastname(String lastname) {
        Response response = context.getResponse();
        String actual = response.jsonPath().getString("lastname");
        if (actual == null) actual = response.jsonPath().getString("booking.lastname");
        Assert.assertEquals(actual, lastname);
    }

    @Then("the response should contain totalprice {int}")
    public void responseShouldContainTotalprice(int price) {
        Response response = context.getResponse();
        Integer actual = response.jsonPath().get("totalprice");
        if (actual == null) actual = response.jsonPath().get("booking.totalprice");
        Assert.assertEquals(actual.intValue(), price);
    }

    @Then("the booking details should match the request")
    public void bookingDetailsShouldMatchRequest() {
        Booking sent = context.getBooking();
        Response response = context.getResponse();
        String firstName = response.jsonPath().getString("booking.firstname");
        String lastName = response.jsonPath().getString("booking.lastname");
        Integer price = response.jsonPath().getInt("booking.totalprice");

        Assert.assertEquals(firstName, sent.getFirstname());
        Assert.assertEquals(lastName, sent.getLastname());
        Assert.assertEquals(price, sent.getTotalprice());
    }

    @Then("the booking should no longer exist when I GET it")
    public void bookingShouldNoLongerExist() {
        Response response = BookingEndpoint.getBookingById(context.getBookingId());
        Assert.assertEquals(response.getStatusCode(), 404,
                "Deleted booking should return 404");
    }

    @Then("the response should match schema {string}")
    public void responseShouldMatchSchema(String schemaFile) {
        context.getResponse().then().assertThat()
                .body(io.restassured.module.jsv.JsonSchemaValidator
                        .matchesJsonSchemaInClasspath("schemas/" + schemaFile));
        logger.info("Schema validation passed: {}", schemaFile);
    }
}
