package space.nanobreaker.configuration.monolith.resources;

import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import io.restassured.config.CsrfConfig;
import io.restassured.http.ContentType;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.CsrfConfig.csrfConfig;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.config.SessionConfig.sessionConfig;
import static org.hamcrest.Matchers.matchesPattern;

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
                        decoderConfig()
                                .defaultContentCharset("UTF-8")
                )
                .sessionConfig(
                        sessionConfig()
                                .sessionIdName("q_session")
                )
                .csrfConfig(
                        csrfConfig()
                                .with().csrfInputFieldName("csrf-token")
                                .and().loggingEnabled()
                                .and().csrfTokenPath("/")
                                .csrfPrioritization(CsrfConfig.CsrfPrioritization.FORM)
                );
    }

    protected final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    // @formatter:off
    protected String csrfToken() {
        return
                given()
                        .when()
                        .get("/")
                        .then()
                        .extract().cookie("csrf-token");
    }
    // @formatter:on

    protected Todo createAndGetTodo(
            final String title,
            final String description,
            final LocalDateTime start,
            final LocalDateTime end
    ) {
        final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("dd/MM/yyyy HH:mm")
                .toFormatter();
        final String username = "alice";
        final String accessToken = keycloakClient.getAccessToken(username);
        final String csrfToken = csrfToken();
        final Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("description", description);
        params.put("start", start.format(formatter));
        params.put("end", end.format(formatter));

        // @formatter:off
        final String location =
                given()
                    .auth().oauth2(accessToken)
                    .contentType(ContentType.URLENC)
                    .formParams(params)
                    .cookie("csrf-token", csrfToken)
                .when()
                    .post("/todos")
                .then()
                    .assertThat()
                    .statusCode(201)
                    .header("Location", matchesPattern(".+/todos/\\d+"))
                .extract().header("Location");
        // @formatter:on
        final String id = location.split("/todos/")[1];

        return new Todo(
                new TodoId(Integer.parseInt(id), username),
                title,
                description,
                start,
                end,
                TodoState.ACTIVE
        );

    }

    protected String getTodoAsHtml(final TodoId id) {
        final String accessToken = keycloakClient.getAccessToken(id.getUsername());
        final String csrfToken = csrfToken();

        // @formatter:off
        return given()
            .auth().oauth2(accessToken)
            .contentType(ContentType.URLENC)
            .cookie("csrf-token", csrfToken)
        .when()
            .get("/todos/" + id.getId())
        .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.HTML)
        .extract().body().asString();
        // @formatter:on
    }

}
