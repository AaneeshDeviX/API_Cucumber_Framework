package com.api.stepdefinitions;

import com.api.config.ConfigReader;
import com.api.endpoints.AuthEndpoint;
import com.api.models.AuthRequest;
import com.api.utils.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class AuthSteps {

    private static final Logger logger = LogManager.getLogger(AuthSteps.class);
    private final TestContext context;
    private AuthRequest authRequest;

    public AuthSteps(TestContext context) {
        this.context = context;
    }

    @Given("I have valid admin credentials")
    public void iHaveValidAdminCredentials() {
        authRequest = new AuthRequest(ConfigReader.getUsername(), ConfigReader.getPassword());
        context.set("authRequest", authRequest);
        logger.info("Valid admin credentials prepared");
    }

    @Given("I have invalid username {string} and password {string}")
    public void iHaveInvalidCredentials(String username, String password) {
        authRequest = new AuthRequest(username, password);
        context.set("authRequest", authRequest);
    }

    @Then("the response should contain a valid token")
    public void responseShouldContainValidToken() {
        Response response = context.getResponse();
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should not be null");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
        Assert.assertTrue(token.length() >= 10, "Token should be at least 10 chars");
        context.setToken(token);
        logger.info("Valid token received: {}", token);
    }

    @Then("the response should contain reason {string}")
    public void responseShouldContainReason(String expectedReason) {
        Response response = context.getResponse();
        String reason = response.jsonPath().getString("reason");
        Assert.assertEquals(reason, expectedReason);
    }

    @Then("I save the token from response")
    public void iSaveTokenFromResponse() {
        Response response = context.getResponse();
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token must be present");
        context.setToken(token);
        logger.info("Token saved to context");
    }

    @Given("I have a valid authentication token")
    public void iHaveValidAuthToken() {
        Response response = AuthEndpoint.createToken(
                ConfigReader.getUsername(),
                ConfigReader.getPassword()
        );
        Assert.assertEquals(response.getStatusCode(), 200);
        String token = response.jsonPath().getString("token");
        context.setToken(token);
        logger.info("Background: Token obtained = {}", token);
    }

    @Given("I have an invalid authentication token {string}")
    public void iHaveInvalidAuthToken(String token) {
        context.setToken(token);
    }
}
