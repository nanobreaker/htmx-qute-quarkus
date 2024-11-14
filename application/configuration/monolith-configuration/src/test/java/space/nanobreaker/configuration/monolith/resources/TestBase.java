package space.nanobreaker.configuration.monolith.resources;

import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

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
                        sessionConfig().sessionIdName("q_session")
                );
    }

    protected final KeycloakTestClient keycloakClient = new KeycloakTestClient();
    protected final String USERNAME = "alice";
    protected final String USER_TIME_ZONE = URLEncoder.encode("Europe/Chisinau", StandardCharsets.UTF_8);
    protected final ZoneId USER_TIME_ZONE_ID = ZoneId.of("Europe/Chisinau");
    protected final ZoneId UTC_TIME_ZONE = ZoneId.of("UTC");
    protected String ACCESS_TOKEN = null;
    protected String CSRF_TOKEN = null;

    @BeforeEach
    protected void setUp() {
        this.ACCESS_TOKEN = keycloakClient.getAccessToken("alice");
        this.CSRF_TOKEN = given().when().get("/").then().extract().cookie("csrf-token");
    }
}
