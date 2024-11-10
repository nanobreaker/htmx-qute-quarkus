package space.nanobreaker.configuration.monolith.resources;

import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.config.SessionConfig.sessionConfig;

public class TestBase {

    static {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config()
                .encoderConfig(
                        encoderConfig()
                                .appendDefaultContentCharsetToContentTypeIfUndefined(false)
                )
                .decoderConfig(
                        decoderConfig().defaultContentCharset("UTF-8")
                )
                .sessionConfig(
                        sessionConfig()
                                .sessionIdName("q_session")
                );
    }

    protected final KeycloakTestClient keycloakClient = new KeycloakTestClient();
    protected String USERNAME = "alice";
    protected String ACCESS_TOKEN = null;
    protected String CSRF_TOKEN = null;
    protected String TIME_ZONE = URLEncoder.encode("Etc/UTC", StandardCharsets.UTF_8);

    protected String access_token() {
        return keycloakClient.getAccessToken("alice");
    }

    // @formatter:off
    protected String csrf_token() {
        return
            given()
            .when()
                .get("/")
            .then()
                .extract().cookie("csrf-token");
    }
    // @formatter:on

    @BeforeEach
    protected void setUp() {
        this.ACCESS_TOKEN = keycloakClient.getAccessToken("alice");
        this.CSRF_TOKEN = given().when().get("/").then().extract().cookie("csrf-token");
    }
}
