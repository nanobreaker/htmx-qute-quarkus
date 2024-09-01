package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.CsrfConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.resources.todo.TodoTemplates;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.CsrfConfig.csrfConfig;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.config.SessionConfig.sessionConfig;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CommandResourceTest {

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

    @Test
    public void shouldCreateAndGetTodo() {
        final String csrfToken = csrfToken();

        final Todo todo = new Todo(
                new TodoId(),
                "yoga",
                "test",
                TodoState.ACTIVE,
                LocalDateTime.of(2024, 8, 10, 0, 0),
                LocalDateTime.of(2024, 8, 11, 0, 0)
        );
        final TodoId todoId = createTodo(todo, csrfToken);
        todo.getId().setId(todoId.getId());
        todo.getId().setUsername(todoId.getUsername());

        // @formatter:off
        final Response todoGetResponse =
            given()
                .contentType(ContentType.URLENC)
                .cookie("csrf-token", csrfToken)
            .when()
                .get("/todo/" + todoId.getId())
            .then()
                .assertThat()
                    .contentType(ContentType.HTML)
                    .statusCode(200)
            .extract().response();
        // @formatter:on

        final String expectedTodoHtml = TodoTemplates.todo(todo, false).render();
        final String actualTodoHtml = todoGetResponse.body().print();
        assertThat(actualTodoHtml).isEqualTo(expectedTodoHtml);
    }

    private TodoId createTodo(
            final Todo todo,
            final String csrfToken
    ) {
        final String query = "todo create \"%s\" -d\"%s\" -s\"%s\" -e\"%s\""
                .formatted(
                        todo.getTitle(),
                        todo.getDescription().orElseThrow(),
                        todo.getStart()
                                .map(start -> start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .orElseThrow(),
                        todo.getEnd()
                                .map(end -> end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .orElseThrow()
                );
        final Map<String, String> params = Map
                .of("command", query);

        // @formatter:off
        final Response todoCreateResponse =
                given()
                    .contentType(ContentType.URLENC)
                    .formParams(params)
                    .cookie("csrf-token", csrfToken)
                .when()
                    .post("/command/execute")
                .then()
                    .assertThat()
                    .statusCode(201)
                .extract().response();
        // @formatter:on

        final String location = todoCreateResponse.getHeader("Location");
        final String id = location.replaceAll(".*/", ""); // Extract the id from the URL
        return new TodoId(
                Integer.parseInt(id),
                "alice"
        );

    }

    // @formatter:off
    private String csrfToken() {
        return
            given()
            .when()
                .get("/")
            .then()
                .extract().cookie("csrf-token");
    }
    // @formatter:on
}
