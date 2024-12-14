package space.nanobreaker.configuration.monolith.resources;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.validation.Valid;
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
import space.nanobreaker.core.domain.v1.Command;
import space.nanobreaker.core.domain.v1.Query;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoError;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.library.error.Error;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@Path("todos")
public class TodosResource {

    private final EventBus eventBus;
    private final JsonWebToken jwt;
    private final Template todosTemplate;

    public TodosResource(
            final EventBus eventBus,
            final JsonWebToken jwt,
            @Location("todos/todos.qute.html") final Template todos
    ) {
        this.eventBus = eventBus;
        this.jwt = jwt;
        this.todosTemplate = todos;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> todos(@CookieParam("time-zone") String zone) {
        var username = (String) jwt.getClaim("upn");
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var query = new Query.Todo.List.All(username);

        Uni<Result<Set<Todo>, Error>> resultUni = eventBus
                .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                .map(Message::body);

        return resultUni.map(result -> switch (result) {
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
            @CookieParam("time-zone") final String zone,
            @QueryParam("id") final Set<Integer> ids,
            @QueryParam("filters") final Set<String> filters
    ) {
        var username = (String) jwt.getClaim("upn");
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
        var query = new Query.Todo.List.ByIdsAndFilters(idz, filters);

        Uni<Result<Set<Todo>, Error>> resultUni = eventBus
                .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                .map(Message::body);

        return resultUni.map(result -> switch (result) {
            case Ok(Set<Todo> todos) -> {
                var html = this.todosTemplate
                        .getFragment("items")
                        .data("todos", todos)
                        .data("zoneId", zoneId)
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
            @CookieParam("time-zone") final String zone,
            @PathParam("id") final Integer id
    ) {
        var username = (String) jwt.getClaim("upn");
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var todoId = new TodoId(id, username);
        var query = new Query.Todo.Get.ById(todoId);
        var resultUni = eventBus
                .<Result<Todo, Error>>request("query.todo.get", query)
                .map(Message::body);

        return resultUni.map(result -> switch (result) {
            case Ok(Todo todo) -> {
                var html = this.todosTemplate
                        .getFragment("item")
                        .data("todo", todo)
                        .data("zoneId", zoneId)
                        .render();

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
    public Uni<Response> create(@Valid @BeanParam final TodoCreateRequest request) {
        var username = (String) jwt.getClaim("upn");
        var zone = URLDecoder.decode(request.zone(), StandardCharsets.UTF_8);
        var zoneId = ZoneId.of(zone);
        var title = request.title();
        var description = request.getDescription();
        var start = request.getStart().map(s -> s.atZone(ZoneId.of(zone)));
        var end = request.getEnd().map(e -> e.atZone(ZoneId.of(zone)));
        var command = new Command.Todo.Create(
                username,
                title,
                description,
                start,
                end
        );

        Uni<Result<Todo, Error>> resultUni = eventBus
                .<Result<Todo, Error>>request("command.todo.create", command)
                .map(Message::body);

        return resultUni.map(result -> switch (result) {
            case Ok(Todo todo) -> {
                var id = todo.getId().getId();
                var location = URI.create("/todos/%s".formatted(id));
                var html = this.todosTemplate
                        .getFragment("item")
                        .data("todo", todo)
                        .data("zoneId", zoneId)
                        .render();

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
        var username = (String) jwt.getClaim("upn");
        var zone = URLDecoder.decode(request.zone(), StandardCharsets.UTF_8);
        var start = request.getStart().map(dt -> dt.atZone(ZoneId.of(zone)));
        var end = request.getEnd().map(dt -> dt.atZone(ZoneId.of(zone)));
        var id = new TodoId(request.id(), username);
        var payload = new Command.Todo.Update.Payload(
                request.getTitle(),
                request.getDescription(),
                start,
                end
        );
        var command = new Command.Todo.Update.ByIds(Set.of(id), payload);
        var resultUni = eventBus
                .<Result<Void, Error>>request("command.todo.update", command)
                .map(Message::body);

        return resultUni.map(result -> switch (result) {
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
        var username = (String) jwt.getClaim("upn");
        var todoId = new TodoId(id, username);
        var command = new Command.Todo.Delete.ByIds(Set.of(todoId));

        Uni<Result<Void, Error>> resultUni = eventBus
                .<Result<Void, Error>>request("command.todo.delete", command)
                .map(Message::body);

        return resultUni.map(result -> switch (result) {
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
