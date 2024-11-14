package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.resources.TestBase;
import space.nanobreaker.configuration.monolith.templates.TodoTemplates;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CommandResourceTest extends TestBase {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Test
    public void post() {
        var title = "title";
        var description = "description";
        var start = LocalDateTime.of(2024, 11, 10, 0, 0);
        var end = LocalDateTime.of(2024, 11, 11, 0, 0);
        var query = "todo create \"%s\" -d\"%s\" -s\"%s\" -e\"%s\""
                .formatted(title, description, start.format(formatter), end.format(formatter));

        // @formatter:off
        var postResponse =
                given()
                    .auth().oauth2(ACCESS_TOKEN)
                    .header("X-CSRF-TOKEN", CSRF_TOKEN)
                    .cookie("csrf-token", CSRF_TOKEN)
                    .cookie("time-zone", USER_TIME_ZONE)
                    .contentType(ContentType.URLENC)
                    .formParams(Map.of("command", query))
                .when()
                    .post("/commands/submit")
                .then()
                    .assertThat()
                    .statusCode(201);
        // @formatter:on

        var id = postResponse.extract().header("Location").split("/todos/")[1];
        var expectedTodoId = new TodoId(Integer.parseInt(id), USERNAME);
        var expectedTodo = new Todo(
                expectedTodoId,
                title,
                description,
                start.atZone(USER_TIME_ZONE_ID),
                end.atZone(USER_TIME_ZONE_ID)
        );
        var expectedHtml = TodoTemplates.todo(expectedTodo, USER_TIME_ZONE_ID).render();
        var actualHtml = postResponse.extract().body().asString();

        assertThat(actualHtml).isEqualTo(expectedHtml);
    }
}
