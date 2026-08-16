package com.api.hooks;

import com.api.utils.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {

    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("════════════════════════════════════════");
        logger.info("▶ SCENARIO: {}", scenario.getName());
        logger.info("  Tags: {}", scenario.getSourceTagNames());
        logger.info("════════════════════════════════════════");
    }

    @After
    public void afterScenario(Scenario scenario) {
        // Attach the last response to the report
        Response response = context.getResponse();
        if (response != null) {
            String responseBody = response.getBody().asString();
            int statusCode = response.getStatusCode();
            long responseTime = response.getTime();

            scenario.attach(
                    "Status: " + statusCode + " | Time: " + responseTime + "ms\n\n" + responseBody,
                    "text/plain",
                    "API Response"
            );

            // Attach JSON body separately for nice formatting
            if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
                scenario.attach(responseBody, "application/json", "JSON Body");
            }
        }

        if (scenario.isFailed()) {
            logger.error("✘ FAILED: {}", scenario.getName());
        } else {
            logger.info("✔ PASSED: {}", scenario.getName());
        }

        // Clear context for next scenario
        context.clear();

        logger.info("════════════════════════════════════════\n");
    }
}
