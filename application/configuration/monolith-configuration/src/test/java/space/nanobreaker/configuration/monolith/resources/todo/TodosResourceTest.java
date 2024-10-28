package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.resources.TestBase;
import space.nanobreaker.configuration.monolith.templates.TodoTemplates;
import space.nanobreaker.core.domain.v1.todo.Todo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
public class TodosResourceTest extends TestBase {

    @Test
    public void shouldCreateTodo() {
        final String accessToken = keycloakClient.getAccessToken("alice");
        final String csrfToken = csrfToken();
        final Map<String, String> params = Map.of("title", "test");

        // @formatter:off
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
                .header("Location", matchesPattern(".+/todos/\\d+"));
        // @formatter:on
    }

    @Test
    public void shouldGetTodo() {
        final String username = "alice";
        final String accessToken = keycloakClient.getAccessToken(username);
        final String csrfToken = csrfToken();
        final Todo todo = this.createAndGetTodo(
                "test title",
                "test description",
                LocalDateTime.of(2024, 1, 1, 12, 0),
                LocalDateTime.of(2024, 1, 2, 16, 0)
        );
        final String expectedHtml = TodoTemplates.todo(todo).render();

        // @formatter:off
        given()
            .auth().oauth2(accessToken)
            .cookie("csrf-token", csrfToken)
        .when()
            .get("/todos/" + todo.getId().getId())
        .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.HTML)
            .body(equalTo(expectedHtml));
        // @formatter:on
    }

    @Test
    public void shouldUpdateTodo() {
        final String username = "alice";
        final String accessToken = keycloakClient.getAccessToken(username);
        final String csrfToken = csrfToken();
        final Todo todo = this.createAndGetTodo(
                "test title",
                "test description",
                LocalDateTime.of(2024, 1, 1, 12, 0),
                LocalDateTime.of(2024, 1, 2, 16, 0)
        );

        final Map<String, String> params = new HashMap<>();
        params.put("title", "new title");
        params.put("description", "new description");

        // @formatter:off
        given()
            .auth().oauth2(accessToken)
            .contentType(ContentType.URLENC)
            .formParams(params)
            .cookie("csrf-token", csrfToken)
        .when()
            .patch("/todos/" + todo.getId().getId())
        .then()
            .assertThat()
            .statusCode(204);
        // @formatter:on

        todo.setTitle("new title");
        todo.setDescription("new description");
        final String expectedHtml = TodoTemplates.todo(todo).render();
        final String actualHtml = this.getTodoAsHtml(todo.getId());

        assertThat(expectedHtml).isEqualTo(actualHtml);
    }

    @Test
    public void shouldDeleteTodo() {
        final String username = "alice";
        final String accessToken = keycloakClient.getAccessToken(username);
        final String csrfToken = csrfToken();
        final Todo todo = this.createAndGetTodo(
                "test title",
                "test description",
                LocalDateTime.of(2024, 1, 1, 12, 0),
                LocalDateTime.of(2024, 1, 2, 16, 0)
        );

        // @formatter:off
        given()
            .auth().oauth2(accessToken)
            .cookie("csrf-token", csrfToken)
        .when()
            .delete("/todos/" + todo.getId().getId())
        .then()
            .assertThat()
            .statusCode(200);
        // @formatter:on

        // @formatter:off
        given()
            .auth().oauth2(accessToken)
            .cookie("csrf-token", csrfToken)
        .when()
            .get("/todos/" + todo.getId().getId())
        .then()
            .assertThat()
            .statusCode(404);
        // @formatter:on
    }

}
