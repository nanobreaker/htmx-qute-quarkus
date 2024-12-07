package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.resources.TestBase;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
public class TodosResourceTest extends TestBase {

    @Location("todos/todos.qute.html") Template todosTemplate;

    @Test
    public void post() {
        // @formatter:off
        given()
            .auth().oauth2(ACCESS_TOKEN)
            .header("X-CSRF-TOKEN", CSRF_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
            .contentType(ContentType.URLENC)
            .formParams(
                   TodoParams.params()
                           .title("test")
                           .description("test")
                           .start(LocalDateTime.of(2024, 11, 10, 0, 0))
                           .end(LocalDateTime.of(2024, 11, 10, 0, 0))
                           .build()
            )
        .when()
            .post("/todos")
        .then()
            .assertThat()
            .statusCode(201)
            .contentType(ContentType.HTML)
            .header("Location", matchesPattern(".+/todos/\\d+"));
        // @formatter:on
    }

    @Test
    public void get() {
        // @formatter:off
        var title = "test";
        var description = "test";
        var start = LocalDateTime.of(2024, 11, 10, 0, 0);
        var end = LocalDateTime.of(2024, 11, 11, 0, 0);
        var postResponse = given()
            .auth().oauth2(ACCESS_TOKEN)
            .header("X-CSRF-TOKEN", CSRF_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
            .contentType(ContentType.URLENC)
            .formParams(
                    TodoParams.params()
                            .title(title)
                            .description(description)
                            .start(start)
                            .end(end)
                            .build()
            )
        .when()
            .post("/todos")
        .then()
            .assertThat()
            .statusCode(201)
            .contentType(ContentType.HTML)
            .header("Location", matchesPattern(".+/todos/\\d+"));
        // @formatter:on

        var id = postResponse.extract().header("Location").split("/todos/")[1];

        // @formatter:off
        var getResponse = given()
            .auth().oauth2(ACCESS_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
        .when()
            .get("/todos/%s".formatted(id))
        .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.HTML);
        // @formatter:on

        var expectedHtml = postResponse.extract().body().asPrettyString();
        var actualHtml = getResponse.extract().body().asPrettyString();
        assertThat(actualHtml).isEqualTo(expectedHtml);
    }

    @Test
    public void patch() {
        // @formatter:off
        var title = "title";
        var description = "description";
        var start = LocalDateTime.of(2024, 11, 10, 0, 0);
        var end = LocalDateTime.of(2024, 11, 11, 0, 0);
        var postResponse = given()
            .auth().oauth2(ACCESS_TOKEN)
            .header("X-CSRF-TOKEN", CSRF_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
            .contentType(ContentType.URLENC)
            .formParams(
                    TodoParams.params()
                            .title(title)
                            .description(description)
                            .start(start)
                            .end(end)
                            .build()
            )
            .when()
            .post("/todos")
            .then()
            .assertThat()
            .statusCode(201)
            .contentType(ContentType.HTML)
            .header("Location", matchesPattern(".+/todos/\\d+"));
        // @formatter:on

        var id = postResponse.extract().header("Location").split("/todos/")[1];
        var updated_title = "updated title";
        var updated_description = "updated description";
        var updated_start = LocalDateTime.of(2024, 12, 10, 0, 0);
        var updated_end = LocalDateTime.of(2024, 12, 11, 0, 0);
        // @formatter:off
        given()
            .auth().oauth2(ACCESS_TOKEN)
            .header("X-CSRF-TOKEN", CSRF_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
            .contentType(ContentType.URLENC)
            .formParams(
                    TodoParams.params()
                        .title(updated_title)
                        .description(updated_description)
                        .start(updated_start)
                        .end(updated_end)
                        .build()
            )
        .when()
            .patch("/todos/%s".formatted(id))
        .then()
            .assertThat()
            .statusCode(204);
        // @formatter:on

        // @formatter:off
        var getResponse = given()
            .auth().oauth2(ACCESS_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
        .when()
            .get("/todos/%s".formatted(id))
        .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.HTML);
        // @formatter:on

        var expectedTodoId = new TodoId(Integer.parseInt(id), USERNAME);
        var expectedTodo = new Todo(
                expectedTodoId,
                updated_title,
                updated_description,
                updated_start.atZone(USER_TIME_ZONE_ID),
                updated_end.atZone(USER_TIME_ZONE_ID)
        );
        var expectedHtml = this.todosTemplate
                .getFragment("item")
                .data("todo", expectedTodo)
                .data("zoneId", USER_TIME_ZONE_ID)
                .render();
        var actualHtml = getResponse.extract().body().asString();

        assertThat(actualHtml).isEqualTo(expectedHtml);
    }

    @Test
    public void delete() {
        // @formatter:off
        var id = given()
            .auth().oauth2(ACCESS_TOKEN)
            .header("X-CSRF-TOKEN", CSRF_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
            .contentType(ContentType.URLENC)
            .formParams(
                    TodoParams.params()
                            .title("test")
                            .description("test")
                            .start(LocalDateTime.of(2024, 11, 10, 0, 0))
                            .end(LocalDateTime.of(2024, 11, 10, 0, 0))
                            .build()
            )
        .when()
            .post("/todos")
        .then()
            .assertThat()
            .statusCode(201)
            .contentType(ContentType.HTML)
            .header("Location", matchesPattern(".+/todos/\\d+"))
        .extract()
            .header("Location")
            .split("/todos/")[1];
        // @formatter:on

        // @formatter:off
        given()
            .auth().oauth2(ACCESS_TOKEN)
            .header("X-CSRF-TOKEN", CSRF_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
        .when()
            .delete("/todos/%s".formatted(id))
        .then()
            .assertThat()
            .statusCode(200);
        // @formatter:on

        // @formatter:off
        given()
            .auth().oauth2(ACCESS_TOKEN)
            .cookie("csrf-token", CSRF_TOKEN)
            .cookie("time-zone", USER_TIME_ZONE)
        .when()
            .get("/todos/%s".formatted(id))
        .then()
            .assertThat()
            .statusCode(404);
        // @formatter:on
    }
}
