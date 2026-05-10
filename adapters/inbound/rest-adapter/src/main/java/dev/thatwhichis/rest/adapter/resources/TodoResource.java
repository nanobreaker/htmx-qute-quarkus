package java.dev.thatwhichis.rest.adapter.resources;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.jboss.resteasy.reactive.Cache;
import java.dev.thatwhichis.rest.adapter.templates.GlobalTemplates;
import space.nanobreaker.core.domain.v1.Command;
import space.nanobreaker.core.domain.v1.Query;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoError;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.tuple.Pair;

@Path("todo")
public class TodoResource {

    private final EventBus eventBus;

    //@formatter:off
    @CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record createTodo()                     implements TemplateInstance {}
    @CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record viewTodos(Set<Todo> todos)       implements TemplateInstance {}
    @CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record viewTodos$items(Set<Todo> todos) implements TemplateInstance {}
    @CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record viewTodos$item(Todo todo)        implements TemplateInstance {}
    @CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record deleteTodos(Set<Integer> ids)    implements TemplateInstance {}
    @CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    public record deleteAllTodos()                 implements TemplateInstance {}

    public record TodoCreateRequest(
            @FormParam("title") @NotBlank String title,
            @FormParam("description") String description,
            @FormParam("start") LocalDateTime start,
            @FormParam("end") LocalDateTime end
    ) {
        public Option<String> getDescription() {return Option.some(description);}
        public Option<LocalDateTime> getStart() {return Option.some(start);}
        public Option<LocalDateTime> getEnd() {return Option.some(end);}
    }

    public record TodoUpdateRequest(
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("start") LocalDateTime start,
            @FormParam("end") LocalDateTime end
    ) {
        public Option<String> getTitle() {return Option.some(title);}
        public Option<String> getDescription() {return Option.some(description);}
        public Option<LocalDateTime> getStart() {return Option.some(start);}
        public Option<LocalDateTime> getEnd() {return Option.some(end);}
    }
    //@formatter:on

    public TodoResource(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @GET
    @WithSpan
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> todos(@Claim("upn") String username) {
        var query = new Query.Todo.List.All(username);

        var reply = eventBus
                .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(var todos) -> Response.ok()
                    .entity(new viewTodos(todos).render())
                    .build();
            case Err(var err) -> Response.serverError()
                    .entity(new GlobalTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @GET
    @WithSpan
    @Path("search")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> search(
            @Claim("upn") String username,
            @QueryParam("id") Set<Integer> ids,
            @QueryParam("filters") Set<String> filters
    ) {
        var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
        var query = switch (Pair.of(idz, filters)) {
            case Pair(var i, var f) when i.isEmpty() && f.isEmpty() -> new Query.Todo.List.All(username);
            case Pair(var i, var f) when f.isEmpty() -> new Query.Todo.List.ByIds(i);
            case Pair(var i, var f) when i.isEmpty() -> new Query.Todo.List.ByFilters(username, f);
            case Pair(var _, var _) -> new Query.Todo.List.ByIdsAndFilters(idz, filters);
        };

        var reply = eventBus
                .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(var todos) -> Response.ok()
                    .entity(new viewTodos$items(todos).render())
                    .build();
            case Err(var err) -> Response.serverError()
                    .entity(new GlobalTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @GET
    @WithSpan
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> get(
            @Claim("upn") String username,
            @PathParam("id") Integer id
    ) {
        var todoId = new TodoId(id, username);
        var query = new Query.Todo.Get.ById(todoId);

        var reply = eventBus
                .<Result<Todo, Error>>request("query.todo.get", query)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(Todo todo) -> Response.ok()
                    .entity(new viewTodos$item(todo).render())
                    .build();
            case Err(Error err) -> switch (err) {
                case TodoError.NotFound _ -> Response.status(Response.Status.NOT_FOUND).build();
                default -> Response.serverError()
                        .entity(new GlobalTemplates.error(err.toString()).render())
                        .build();
            };
        });
    }

    @POST
    @WithSpan
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> create(
            @Claim("upn") String username,
            @CookieParam("time-zone") String zone,
            @Valid @BeanParam TodoCreateRequest request
    ) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var title = request.title();
        var description = request.getDescription();
        var start = request.getStart().map(s -> s.atZone(zoneId));
        var end = request.getEnd().map(e -> e.atZone(zoneId));
        var command = new Command.Todo.Create(
                username,
                title,
                description,
                start,
                end
        );

        var reply = eventBus
                .<Result<Todo, Error>>request("command.todo.create", command)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(Todo todo) -> {
                var id = todo.getId().getId();
                var location = URI.create("/todo/%s".formatted(id));
                var html = new viewTodos$item(todo).render();

                yield Response.created(location)
                        .entity(html)
                        .build();
            }
            case Err(Error err) -> Response.serverError()
                    .entity(new GlobalTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @PATCH
    @WithSpan
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> patch(
            @Claim("upn") String username,
            @CookieParam("time-zone") String zone,
            @PathParam("id") Integer pathId,
            @Valid @BeanParam TodoUpdateRequest request
    ) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var start = request.getStart().map(dt -> dt.atZone(zoneId));
        var end = request.getEnd().map(dt -> dt.atZone(zoneId));
        var id = new TodoId(pathId, username);
        var payload = new Command.Todo.Update.Payload(
                request.getTitle(),
                request.getDescription(),
                start,
                end
        );
        var command = new Command.Todo.Update.ByIds(Set.of(id), payload);

        var reply = eventBus
                .<Result<Void, Error>>request("command.todo.update", command)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(_) -> Response.noContent().build();
            case Err(Error err) -> Response.serverError()
                    .entity(new GlobalTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @DELETE
    @Path("{id}")
    @WithSpan
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> delete(
            @Claim("upn") String username,
            @PathParam("id") Integer id
    ) {
        var todoId = new TodoId(id, username);
        var command = new Command.Todo.Delete.ByIds(Set.of(todoId));

        var reply = eventBus
                .<Result<Void, Error>>request("command.todo.delete", command)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(_) -> Response.ok().build();
            case Err(Error err) -> Response.serverError()
                    .entity(new GlobalTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @GET
    @Path("create")
    @WithSpan
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> getForm() {
        var template = new createTodo();

        return template.createUni();
    }
}
