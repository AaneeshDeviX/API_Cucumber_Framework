package com.api.utils;

import com.api.models.Booking;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Context - shared state across step definitions for one scenario.
 * Allows step definitions to pass data (token, response, IDs) between each other.
 */
public class TestContext {

    private final Map<String, Object> context = new HashMap<>();

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }

    public String getString(String key) {
        Object v = context.get(key);
        return v != null ? v.toString() : null;
    }

    public Integer getInt(String key) {
        Object v = context.get(key);
        if (v instanceof Integer) return (Integer) v;
        if (v != null) return Integer.parseInt(v.toString());
        return null;
    }

    // Convenience methods
    public void setToken(String token)        { set("token", token); }
    public String getToken()                  { return getString("token"); }

    public void setResponse(Response response){ set("response", response); }
    public Response getResponse()             { return (Response) get("response"); }

    public void setBookingId(Integer id)      { set("bookingId", id); }
    public Integer getBookingId()             { return getInt("bookingId"); }

    public void setBooking(Booking booking)   { set("booking", booking); }
    public Booking getBooking()               { return (Booking) get("booking"); }

    public void clear() {
        context.clear();
    }
}
