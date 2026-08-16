package com.api.endpoints;

import com.api.models.AuthRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Authentication endpoint - obtains access token.
 */
public class AuthEndpoint extends BaseEndpoint {

    private static final String AUTH_PATH = "/auth";

    public static Response createToken(AuthRequest authRequest) {
        logger.info("POST /auth - Generating token for user: {}", authRequest.getUsername());

        return given()
                .spec(getRequestSpec())
                .body(authRequest)
        .when()
                .post(AUTH_PATH);
    }

    public static Response createToken(String username, String password) {
        return createToken(new AuthRequest(username, password));
    }
}
