package space.nanobreaker.configuration.monolith.resources.todo;

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
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class TodoResourceTest {

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
    public void shouldAccessTodoBoardResourceWhenAuthenticated() {
        given()
                .auth().oauth2(keycloakClient.getAccessToken("alice"))
                .when().get("/todo/board")
                .then().statusCode(200)
                .log()
                .all();
    }

    @Test
    public void shouldRedirectToKeycloakWhenNotAuthenticated() {
        given()
                .auth().none()
                .when().get("/todo/board")
                .then().assertThat()
                .statusCode(200)
                .cookie("AUTH_SESSION_ID")
                .cookie("KC_RESTART")
                .body(containsString("Sign in to your account"))
                .log()
                .all();
    }

    @Test
    public void shouldCreateTodo() {
        final Map<String, ? extends Serializable> createTodoFormParams = Map.of(
                "title", "test title",
                "target", "2023-12-21",
                "description", "test description"
        );

        given()
                .auth().oauth2(keycloakClient.getAccessToken("alice"))
                .contentType(ContentType.URLENC)
                .formParams(createTodoFormParams)
                .cookie("csrf-token", csrfToken())
                .when()
                .post("/todo")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(
                        containsString("test title"),
                        containsString("2023-12-21"),
                        containsString("test description")
                )
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