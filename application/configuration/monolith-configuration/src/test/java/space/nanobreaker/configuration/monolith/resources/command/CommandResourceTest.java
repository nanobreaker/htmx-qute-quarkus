package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import io.restassured.config.CsrfConfig;
import io.restassured.config.SessionConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Map;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.CsrfConfig.csrfConfig;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;

@QuarkusTest
public class CommandResourceTest {

    static {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.config = config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
        RestAssured.config = RestAssured.config().decoderConfig(decoderConfig().defaultContentCharset("UTF-8"));
        RestAssured.config = RestAssured.config().sessionConfig(new SessionConfig().sessionIdName("q_session"));
        RestAssured.config = RestAssured.config().csrfConfig(csrfConfig()
                .with().csrfInputFieldName("csrf-token")
                .and().loggingEnabled()
                .and().csrfTokenPath("/")
                .csrfPrioritization(CsrfConfig.CsrfPrioritization.FORM));
    }

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void shouldCreateTodo() {
        final Map<String, ? extends Serializable> createTodoFormParams = Map.of(
                "command", "todo create \"yoga\" -d\"Igor the best yogin in Moldova gives yoga lesson\""
        );

        given()
                .auth().oauth2(keycloakClient.getAccessToken("alice"))
                .contentType(ContentType.URLENC)
                .formParams(createTodoFormParams)
                .cookie("csrf-token", csrfToken())
                .when()
                .post("/command/execute")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().all();
    }

    private String csrfToken() {
        return given()
                .auth().oauth2(keycloakClient.getAccessToken("alice"))
                .when()
                .get("/")
                .then()
                .extract().cookie("csrf-token");
    }
}
