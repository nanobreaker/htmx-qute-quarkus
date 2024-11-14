package space.nanobreaker.configuration.monolith.resources;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.Cache;
import space.nanobreaker.configuration.monolith.dto.TodoCreateRequest;
import space.nanobreaker.configuration.monolith.dto.TodoUpdateRequest;
import space.nanobreaker.configuration.monolith.templates.ErrorTemplates;
import space.nanobreaker.configuration.monolith.templates.TodoTemplates;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.usecases.v1.todo.TodoError;
import space.nanobreaker.core.usecases.v1.todo.command.CreateTodo;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodo;
import space.nanobreaker.core.usecases.v1.todo.command.UpdateTodo;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodoQuery;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodosQuery;
import space.nanobreaker.library.either.Either;
import space.nanobreaker.library.either.Left;
import space.nanobreaker.library.either.Right;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.result.Err;
import space.nanobreaker.library.result.Ok;
import space.nanobreaker.library.result.Result;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@Path("todos")
public class TodosResource {

    @Inject EventBus eventBus;
    @Inject JsonWebToken jwt;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> todos(
            @CookieParam("time-zone") String zone
    ) {
        final var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        final String username = jwt.getClaim("upn");
        final Either<Set<TodoId>, String> usernameOrIds = new Right<>(username);
        final var query = new GetTodosQuery(usernameOrIds);

        final Uni<Result<Set<Todo>, Error>> resultUni = eventBus
                .<Result<Set<Todo>, Error>>request("todos.get", query)
                .map(Message::body);

        return resultUni
                .map(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        var html = TodoTemplates.todos(todos, zoneId).render();

                        yield Response.ok(html)
                                .build();
                    }
                    case Err(Error err) -> {
                        var html = ErrorTemplates.error(err.toString()).render();

                        yield Response.serverError()
                                .entity(html)
                                .build();
                    }
                });
    }

    @GET
    @Path("search")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> search(
            @CookieParam("time-zone") String zone,
            @QueryParam("id") final Set<Integer> ids
    ) {
        final var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        final String username = jwt.getClaim("upn");
        final Set<TodoId> todoIds = ids.stream()
                .map(id -> new TodoId(id, username))
                .collect(Collectors.toSet());
        final Either<Set<TodoId>, String> usernameOrIds = ids.isEmpty()
                ? new Right<>(username)
                : new Left<>(todoIds);
        final GetTodosQuery query = new GetTodosQuery(usernameOrIds);

        final Uni<Result<Set<Todo>, Error>> resultUni = eventBus
                .<Result<Set<Todo>, Error>>request("todos.get", query)
                .map(Message::body);

        return resultUni
                .map(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        var html = TodoTemplates.todos(todos, zoneId)
                                .getFragment("items")
                                .instance()
                                .data("todos", todos)
                                .render();

                        yield Response.ok(html)
                                .build();
                    }
                    case Err(Error err) -> {
                        var html = ErrorTemplates.error(err.toString()).render();

                        yield Response.serverError()
                                .entity(html)
                                .build();
                    }
                });
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> get(
            @CookieParam("time-zone") String zone,
            @PathParam("id") Integer id
    ) {
        final var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        final var todoId = new TodoId(id, jwt.getClaim("upn"));
        final var query = new GetTodoQuery(todoId);

        final Uni<Result<Todo, Error>> resultUni = eventBus
                .<Result<Todo, Error>>request("todo.get", query)
                .map(Message::body);

        return resultUni
                .map(result -> switch (result) {
                    case Ok(Todo todo) -> {
                        // todo: consider using 'item' fragment from todos template?
                        var html = TodoTemplates.todo(todo, zoneId).render();

                        yield Response.ok(html)
                                .build();
                    }
                    case Err(Error err) -> switch (err) {
                        case TodoError.NotFound _ -> {
                            yield Response.status(Response.Status.NOT_FOUND)
                                    .build();
                        }
                        default -> {
                            var html = ErrorTemplates.error(err.toString()).render();

                            yield Response.serverError()
                                    .entity(html)
                                    .build();
                        }
                    };
                });
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> create(@BeanParam final TodoCreateRequest request) {
        final String username = jwt.getClaim("upn");
        final var zone = URLDecoder.decode(request.zone(), StandardCharsets.UTF_8);
        final var zoneId = ZoneId.of(zone);
        final var title = request.title();
        final var description = request.getDescription();
        final var start = request.getStart().map(s -> s.atZone(ZoneId.of(zone)));
        final var end = request.getEnd().map(e -> e.atZone(ZoneId.of(zone)));
        final var command = new CreateTodo(
                username,
                title,
                description,
                start,
                end
        );

        final Uni<Result<Todo, Error>> resultUni = eventBus
                .<Result<Todo, Error>>request("todo.create", command)
                .map(Message::body);

        return resultUni
                .map(result -> switch (result) {
                    case Ok(Todo todo) -> {
                        var id = todo.getId().getId();
                        var location = URI.create("/todos/%s".formatted(id));
                        var html = TodoTemplates.todo(todo, zoneId).render();

                        yield Response.created(location)
                                .entity(html)
                                .build();
                    }
                    case Err(Error err) -> {
                        var html = ErrorTemplates.error(err.toString()).render();

                        yield Response.serverError()
                                .entity(html)
                                .build();
                    }
                });
    }

    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> patch(@BeanParam final TodoUpdateRequest request) {
        final String username = jwt.getClaim("upn");
        final var zone = URLDecoder.decode(request.zone(), StandardCharsets.UTF_8);
        final var start = request.getStart().map(dt -> dt.atZone(ZoneId.of(zone)));
        final var end = request.getEnd().map(dt -> dt.atZone(ZoneId.of(zone)));
        final Either<String, Set<TodoId>> usernameEither = new Left<>(username);
        final var command = new UpdateTodo(
                usernameEither,
                Option.none(),
                request.getTitle(),
                request.getDescription(),
                start,
                end
        );

        final Uni<Result<Void, Error>> resultUni = eventBus
                .<Result<Void, Error>>request("todo.update", command)
                .map(Message::body);

        return resultUni
                .map(result -> switch (result) {
                    case Ok(_) -> {
                        yield Response.noContent().build();
                    }
                    case Err(Error err) -> {
                        var html = ErrorTemplates.error(err.toString()).render();

                        yield Response.serverError()
                                .entity(html)
                                .build();
                    }
                });
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> delete(@PathParam("id") Integer id) {
        final var todoId = new TodoId(id, jwt.getClaim("upn"));
        final var command = new DeleteTodo(todoId);

        final Uni<Result<Void, Error>> resultUni = eventBus
                .<Result<Void, Error>>request("todo.delete", command)
                .map(Message::body);

        return resultUni
                .map(result -> switch (result) {
                    case Ok(_) -> {
                        yield Response.ok()
                                .build();
                    }
                    case Err(Error err) -> {
                        var html = ErrorTemplates.error(err.toString())
                                .render();

                        yield Response.serverError()
                                .entity(html)
                                .build();
                    }
                });
    }

    @GET
    @Path("create")
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> getForm() {
        return TodoTemplates.todoCreate()
                .createUni();
    }
}
