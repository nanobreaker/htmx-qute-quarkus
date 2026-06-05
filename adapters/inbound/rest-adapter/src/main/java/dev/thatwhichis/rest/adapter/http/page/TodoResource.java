package dev.thatwhichis.rest.adapter.http.page;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoError;
import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.core.ports.inbound.todo.TodoCommand;
import dev.thatwhichis.core.ports.inbound.todo.TodoQuery;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.Option;
import dev.thatwhichis.library.tuple.Pair;
import dev.thatwhichis.rest.adapter.qute.templates.ErrorTemplates;
import dev.thatwhichis.rest.adapter.qute.templates.TodoTemplates;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
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
import org.eclipse.microprofile.jwt.Claims;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("todos")
@Authenticated
public class TodoResource {

    private final EventBus eventBus;
    private final SecurityIdentity identity;

    //@formatter:off
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

    @Inject
    public TodoResource(
            final EventBus eventBus,
            final SecurityIdentity identity
    ) {
        this.eventBus = eventBus;
        this.identity = identity;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @WithSpan("todos")
    public Uni<Response> todos(@CookieParam("time-zone") final String zone) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var principal = identity.getPrincipal(OidcJwtCallerPrincipal.class);
        var userId = UUID.fromString(principal.getClaim(Claims.sub));
        var query = new TodoQuery.List.All(userId);

        var reply = eventBus
                .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                .map(Message::body);

        return reply.flatMap(result -> switch (result) {
            case Ok(var todos) -> TodoTemplates.todos(todos, zoneId)
                    .createUni()
                    .map(html -> Response.ok(html).build());
            case Err(var err) -> ErrorTemplates.error(err.toString())
                    .createUni()
                    .map(html -> Response.serverError().entity(html).build());
        });
    }

    @GET
    @Path("search")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> search(
            @QueryParam("id") final Set<Integer> ids,
            @QueryParam("filters") final Set<String> filters,
            @CookieParam("time-zone") final String zone
    ) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var principal = identity.getPrincipal(OidcJwtCallerPrincipal.class);
        var userId = UUID.fromString(principal.getClaim(Claims.sub));
        var idz = ids.stream().map(id -> new TodoId(id, userId)).collect(Collectors.toSet());
        var query = switch (Pair.of(idz, filters)) {
            case Pair(var i, var f) when i.isEmpty() && f.isEmpty() -> new TodoQuery.List.All(userId);
            case Pair(var i, var f) when f.isEmpty() -> new TodoQuery.List.ByIds(i);
            case Pair(var i, var f) when i.isEmpty() -> new TodoQuery.List.ByFilters(userId, f);
            case Pair(var _, var _) -> new TodoQuery.List.ByIdsAndFilters(idz, filters);
        };

        var reply = eventBus
                .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(var todos) -> Response.ok()
                    .entity(TodoTemplates.todos$items(todos, zoneId).render())
                    .build();
            case Err(var err) -> Response.serverError()
                    .entity(ErrorTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> get(
            @PathParam("id") final Integer id,
            @CookieParam("time-zone") final String zone
    ) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var principal = identity.getPrincipal(OidcJwtCallerPrincipal.class);
        var userId = UUID.fromString(principal.getClaim(Claims.sub));
        var todoId = new TodoId(id, userId);
        var query = new TodoQuery.Get.ById(todoId);

        var reply = eventBus
                .<Result<Todo, Error>>request("query.todo.get", query)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(Todo todo) -> Response.ok()
                    .entity(TodoTemplates.todos$item(todo, zoneId).render())
                    .build();
            case Err(Error err) -> switch (err) {
                case TodoError.NotFound _ -> Response.status(Response.Status.NOT_FOUND).build();
                default -> Response.serverError()
                        .entity(ErrorTemplates.error(err.toString()).render())
                        .build();
            };
        });
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> create(
            @CookieParam("time-zone") final String zone,
            @Valid @BeanParam final TodoCreateRequest request
    ) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var principal = identity.getPrincipal(OidcJwtCallerPrincipal.class);
        var userId = UUID.fromString(principal.getClaim(Claims.sub));
        var title = request.title();
        var description = request.getDescription();
        var start = request.getStart().map(s -> s.atZone(zoneId)).map(ZonedDateTime::toInstant);
        var end = request.getEnd().map(e -> e.atZone(zoneId)).map(ZonedDateTime::toInstant);
        var command = new TodoCommand.Create(
                userId,
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
                var id = todo.getId().id();
                var location = URI.create("/todo/%s".formatted(id));
                var html = TodoTemplates.todos$item(todo, zoneId).render();

                yield Response.created(location)
                        .entity(html)
                        .build();
            }
            case Err(Error err) -> Response.serverError()
                    .entity(ErrorTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> patch(
            @CookieParam("time-zone") final String zone,
            @PathParam("id") final Integer pathId,
            @Valid @BeanParam final TodoUpdateRequest request
    ) {
        var principal = identity.getPrincipal(OidcJwtCallerPrincipal.class);
        var userId = UUID.fromString(principal.getClaim(Claims.sub));
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var start = request.getStart().map(dt -> dt.atZone(zoneId)).map(ZonedDateTime::toInstant);
        var end = request.getEnd().map(dt -> dt.atZone(zoneId)).map(ZonedDateTime::toInstant);
        var id = new TodoId(pathId, userId);
        var payload = new TodoCommand.Update.Payload(
                request.getTitle(),
                request.getDescription(),
                start,
                end
        );
        var command = new TodoCommand.Update.ByIds(Set.of(id), payload);

        var reply = eventBus
                .<Result<Void, Error>>request("command.todo.update", command)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(_) -> Response.noContent().build();
            case Err(Error err) -> Response.serverError()
                    .entity(ErrorTemplates.error(err.toString()).render())
                    .build();
        });
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> delete(@PathParam("id") final Integer id) {
        var principal = identity.getPrincipal(OidcJwtCallerPrincipal.class);
        var userId = UUID.fromString(principal.getClaim(Claims.sub));
        var todoId = new TodoId(id, userId);
        var command = new TodoCommand.Delete.ByIds(Set.of(todoId));

        var reply = eventBus
                .<Result<Void, Error>>request("command.todo.delete", command)
                .map(Message::body);

        return reply.map(result -> switch (result) {
            case Ok(_) -> Response.ok().build();
            case Err(Error err) -> Response.serverError()
                    .entity(ErrorTemplates.error(err.toString()).render())
                    .build();
        });
    }
}
