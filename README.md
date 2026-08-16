# REST API Automation — REST Assured + Cucumber

[![API Tests](https://github.com/AaneeshDeviX/API_Cucumber_Framework/actions/workflows/ci.yml/badge.svg)](https://github.com/AaneeshDeviX/API_Cucumber_Framework/actions/workflows/ci.yml)

BDD API suite against the [Restful-Booker](https://restful-booker.herokuapp.com)
public API. 21 scenarios across 7 feature files, covering authentication, full
booking CRUD, an end-to-end integration flow, and JSON Schema validation of every
response body.

## Stack

| | |
|---|---|
| HTTP client | REST Assured 5.5.0 |
| BDD | Cucumber 7.20.1 (Java) |
| Runner | TestNG 7.10.2 |
| Serialisation | Jackson 2.18.2, Lombok |
| Assertions | Hamcrest 3.0 |
| Schema validation | `rest-assured:json-schema-validator` |
| Reporting | ExtentReports 5.1.2 (Spark adapter) |
| CI | GitHub Actions — every push, plus a weekly scheduled run |

## Running it

```bash
mvn test                                        # all 21 scenarios
mvn test -Dcucumber.filter.tags="@smoke"        # smoke only
```

Containerised, no local JDK or Maven needed:

```bash
docker build -t api-tests .
docker run --rm api-tests
```

Reports land in `target/` — Cucumber HTML plus the Extent Spark report.

## Coverage

| Feature | Scenarios | Covers |
|---|---|---|
| `authentication.feature` | 4 | token creation, bad credentials, token reuse |
| `create_booking.feature` | 4 | valid payloads, field validation, response schema |
| `get_bookings.feature` | 4 | list all, filter by name, filter by date, 404 handling |
| `update_booking.feature` | 4 | full update, partial update, auth required |
| `delete_booking.feature` | 3 | delete, verify gone, delete without auth |
| `health_check.feature` | 1 | service ping |
| `e2e_integration.feature` | 1 | create → read → update → delete in one flow |

## Known defect found by this suite

`Create booking with invalid data type is rejected` posts a non-numeric
`totalprice` of `"abc"`. Restful-Booker responds **200 with a created booking**
rather than rejecting the payload — the API performs no type validation on that
field.

The scenario asserts the *correct* behaviour (a 4xx/5xx rejection), so it fails by
design and documents the defect rather than hiding it. It carries the
`@known-defect` tag and CI runs `not @known-defect`, so the badge tracks
regressions rather than this known API bug.

Run it deliberately with:

```bash
mvn test "-Dcucumber.filter.tags=@known-defect"
```

## Layout

```
src/main/java/com/api/
  endpoints/      # BaseEndpoint + one class per resource — request building lives here
  models/         # POJOs: AuthRequest, Booking, BookingDates, BookingResponse
  config/         # ConfigReader — reads config.properties
  utils/          # JsonUtil, TestContext (shares state across steps)
src/test/java/com/api/
  stepdefinitions/  # Gherkin bindings
  runners/          # TestRunner, SmokeTestRunner
  hooks/            # setup and teardown
src/test/resources/
  features/       # 7 .feature files
  schemas/        # 3 JSON Schema files used for response validation
```

Steps assert on deserialised POJOs rather than raw JSON strings, so a contract
change surfaces as a compile error or a schema failure rather than a silent pass.

## Configuration

`src/test/resources/config.properties` holds the base URL, timeouts and the
Restful-Booker demo credentials (`admin` / `password123` — the published defaults
for that public sandbox). No private credentials are stored here.
