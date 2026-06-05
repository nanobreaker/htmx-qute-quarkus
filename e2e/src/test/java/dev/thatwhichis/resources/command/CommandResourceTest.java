package dev.thatwhichis.resources.command;

import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.core.ports.inbound.user.UserCommand;
import dev.thatwhichis.core.ports.outbound.calendar.CalendarEntryRepository;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.rest.adapter.http.command.CommandResource;
import io.github.dcadea.jresult.Result;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.vertx.VertxContextSupport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.config.HttpClientConfig.httpClientConfig;
import static io.restassured.config.SessionConfig.sessionConfig;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestHTTPEndpoint(CommandResource.class)
public class CommandResourceTest {

    private static final String sub_string = "52b2a821-b00b-43cd-bfcc-88ca20ac241b";
    private static final UUID sub = UUID.fromString(sub_string);
    private static final String upn = "alice";
    private static final String sid = "test-sid";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    static {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config()
                .httpClient(
                        httpClientConfig()
                                .setParam("http.connection.timeout", 10_000)
                                .setParam("http.socket.timeout", 30_000)
                )
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

    protected final String USER_TIME_ZONE = URLEncoder.encode("Europe/Chisinau", StandardCharsets.UTF_8);
    protected final ZoneId USER_TIME_ZONE_ID = ZoneId.of("Europe/Chisinau");

    @Location("todo/todos.html") Template todosTemplate;

    @Inject CalendarEntryRepository calendarEntryRepository;
    @Inject EventBus eventBus;

    @BeforeEach
    public void setUp() throws Throwable {
        var command = new UserCommand.Authenticate(
                sub,
                upn,
                sid,
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        VertxContextSupport.subscribeAndAwait(
                () -> Panache.withTransaction(() -> eventBus.<Result<Void, Error>>request("user.authenticated", command))
        );
    }

    @Test
    @TestSecurity(user = upn, roles = {"user"})
    @OidcSecurity(claims = {
            @Claim(key = "sub", value = sub_string),
            @Claim(key = "upn", value = upn),
            @Claim(key = "sid", value = sid)
    })
    public void post() throws Throwable {
        var title = "title";
        var description = "description";
        var start = LocalDateTime.of(2025, 11, 10, 0, 0);
        var end = LocalDateTime.of(2025, 11, 11, 0, 0);
        var query = "todo create \"%s\" -d\"%s\" -s\"%s\" -e\"%s\""
                .formatted(title, description, start.format(formatter), end.format(formatter));

        // @formatter:off
        var postResponse =
                given()
                    .cookie("time-zone", USER_TIME_ZONE)
                    .contentType(ContentType.URLENC)
                    .formParams(Map.of("command", query))
                .when()
                    .post("/submit")
                .then()
                    .assertThat()
                    .statusCode(201);
        // @formatter:on

        var id = postResponse.extract().header("Location").split("/todos/")[1];
        var expectedTodoId = new TodoId(Integer.parseInt(id), UUID.randomUUID());
        var expectedTodo = new Todo(
                expectedTodoId,
                title,
                description,
                start.atZone(USER_TIME_ZONE_ID).toInstant(),
                end.atZone(USER_TIME_ZONE_ID).toInstant()
        );
        var expectedHtml = this.todosTemplate
                .getFragment("item")
                .data("todo", expectedTodo)
                .data("zone", USER_TIME_ZONE_ID)
                .render();

        var actualHtml = postResponse.extract().body().asString();
        assertThat(actualHtml).isEqualTo(expectedHtml);

        // verify side-effect of todo creation
        var expectedCalendarEntry = new CalendarEntry(1, sub, expectedTodo.getStart(), expectedTodo.getEnd());

        VertxContextSupport.subscribeAndAwait(
                () -> Panache.withTransaction(
                        () -> calendarEntryRepository
                                .get(1)
                                .map(result -> assertThat(result.unwrap())
                                        .usingRecursiveComparison()
                                        .isEqualTo(expectedCalendarEntry)
                                )

                )
        );
    }
}
