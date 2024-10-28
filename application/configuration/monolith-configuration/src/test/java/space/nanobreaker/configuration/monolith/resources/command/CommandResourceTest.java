package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.resources.TestBase;
import space.nanobreaker.configuration.monolith.templates.TodoTemplates;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CommandResourceTest extends TestBase {

    @Test
    public void shouldCreateAndGetTodo() {
        final String accessToken = keycloakClient.getAccessToken("alice");
        final String csrfToken = csrfToken();
        final Todo todo = new Todo(
                new TodoId(),
                "yoga",
                "test",
                LocalDateTime.of(2024, 8, 10, 0, 0),
                LocalDateTime.of(2024, 8, 11, 0, 0),
                TodoState.ACTIVE
        );
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
                    .auth().oauth2(accessToken)
                    .cookie("csrf-token", csrfToken)
                    .contentType(ContentType.URLENC)
                    .formParams(params)
                .when()
                    .post("/commands/submit")
                .then()
                    .assertThat()
                    .statusCode(201)
                .extract().response();
        // @formatter:on

        final String location = todoCreateResponse.getHeader("Location");
        final String id = location.replaceAll(".*/", ""); // Extract the id from the URL
        final TodoId todoId = new TodoId(
                Integer.parseInt(id),
                "alice"
        );
        todo.getId().setId(todoId.getId());
        todo.getId().setUsername(todoId.getUsername());

        // @formatter:off
        final Response todoGetResponse =
            given()
                .auth().oauth2(accessToken)
                .cookie("csrf-token", csrfToken)
            .when()
                .get("/todos/" + todoId.getId())
            .then()
                .assertThat()
                .contentType(ContentType.HTML)
                .statusCode(200)
            .extract().response();
        // @formatter:on

        final String expectedTodoHtml = TodoTemplates.todo(todo).render();
        final String actualTodoHtml = todoGetResponse.body().print();
        assertThat(actualTodoHtml).isEqualTo(expectedTodoHtml);
    }
}
