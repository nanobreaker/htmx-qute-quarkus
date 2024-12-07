package space.nanobreaker.configuration.monolith.services.command;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import space.nanobreaker.configuration.monolith.services.sse.SseEvent;
import space.nanobreaker.configuration.monolith.templates.ErrorTemplates;
import space.nanobreaker.configuration.monolith.templates.HelpTemplates;
import space.nanobreaker.configuration.monolith.templates.TodoTemplates;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.library.error.Error;

import java.net.URI;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommandController {

    @Inject
    EventBus eventBus;

    @Inject
    CommandDescriber describer;

    @Inject
    CommandExecutor executor;

    @Inject
    SecurityIdentity securityIdentity;

    @Location("todos/todos.qute.html")
    Template todosTemplate;

    @Inject
    JsonWebToken jwt;

    public Uni<Response> help(final Command cmd) {
        var text = describer.describe(cmd);
        var html = HelpTemplates.help(text);
        var response = Response.ok()
                .header("HX-Retarget", "#feedback")
                .entity(html)
                .build();

        return Uni.createFrom().item(response);
    }

    public Uni<Response> createTodo(
            final Command.Todo.Create.Default cmd,
            final ZoneId zoneId
    ) {
        Uni<Result<Todo, Error>> resultUni = executor.createTodo(cmd, zoneId);

        return resultUni.map(result -> switch (result) {
            case Ok(Todo todo) -> {
                String upn = jwt.getClaim("upn");
                String sid = jwt.getClaim("sid");
                var location = "/todos/%d".formatted(todo.getId().getId());
                var uri = URI.create(location);
                var html = this.todosTemplate
                        .getFragment("item")
                        .data("todo", todo)
                        .data("zoneId", zoneId)
                        .render();

                var event = new SseEvent.TodoCreated(upn, sid, html);
                eventBus.publish("sse.todo.created", event);

                yield Response.created(uri)
                        .header("HX-Reswap", "beforeend")
                        .header("HX-Trigger", "command.empty")
                        .entity(html)
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    private Uni<Response> __listTodo(
            final Command.Todo.List list,
            final ZoneId zoneId
    ) {
        Uni<Result<Set<Todo>, Error>> resultUni = executor.listTodo(list);

        return resultUni.map(result -> switch (result) {
            case Ok(Set<Todo> todos) -> {
                var html = TodoTemplates.todos(todos, zoneId)
                        .getFragment("items")
                        .instance()
                        .data("todos", todos)
                        .data("zoneId", zoneId)
                        .render();

                yield Response.ok()
                        .header("HX-Trigger", "command.empty")
                        .entity(html)
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    public Uni<Response> listTodo(
            final Command.Todo.List.All all,
            final ZoneId zoneId
    ) {
        return this.__listTodo(all, zoneId);
    }

    public Uni<Response> listTodo(
            final Command.Todo.List.ByIds byIds,
            final ZoneId zoneId
    ) {
        return this.__listTodo(byIds, zoneId);
    }

    public Uni<Response> listTodo(
            final Command.Todo.List.ByFilters byFilters,
            final ZoneId zoneId
    ) {
        return this.__listTodo(byFilters, zoneId);
    }

    public Uni<Response> listTodo(
            final Command.Todo.List.ByIdsAndFilters byIdsAndFilters,
            final ZoneId zoneId
    ) {
        return this.__listTodo(byIdsAndFilters, zoneId);
    }

    public Uni<Response> updateTodo(
            final Command.Todo.Update.ByIds byIds,
            final ZoneId zoneId
    ) {
        Uni<Result<Void, Error>> resultUni = executor.updateTodo(byIds, zoneId);

        return resultUni.map(result -> switch (result) {
            case Ok(Void _) -> {
                String upn = jwt.getClaim("upn");
                String sid = jwt.getClaim("sid");
                var ids = byIds.ids();
                var query = ids.stream().map("id=%d"::formatted).collect(Collectors.joining("&"));
                var location = URI.create("/todos/search?%s".formatted(query));

                ids.forEach(id -> {
                    final var event = new SseEvent.TodoUpdated(upn, sid, id);
                    eventBus.publish("sse.todo.updated", event);
                });

                yield Response.seeOther(location)
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    public Uni<Response> updateTodo(
            final Command.Todo.Update.ByFilters byFilters,
            final ZoneId zoneId
    ) {
        Uni<Result<Void, Error>> resultUni = executor.updateTodo(byFilters, zoneId);

        return resultUni.map(result -> switch (result) {
            case Ok(Void _) -> {
                String upn = jwt.getClaim("upn");
                String sid = jwt.getClaim("sid");
                var filters = byFilters.filters();
                var query = filters.stream().map("filters=%s"::formatted).collect(Collectors.joining("&"));
                var location = URI.create("/todos/search?%s".formatted(query));

                // todo: figure out how to update todos using only filters
                // ids.forEach(id -> {
                //    final var event = new SseEvent.TodoUpdated(upn, sid, id);
                //    eventBus.publish("sse.todo.updated", event);
                // });

                yield Response.seeOther(location)
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    public Uni<Response> updateTodo(
            final Command.Todo.Update.ByIdsAndFilters byIdsAndFilters,
            final ZoneId zoneId
    ) {
        Uni<Result<Void, Error>> resultUni = executor.updateTodo(byIdsAndFilters, zoneId);

        return resultUni.map(result -> switch (result) {
            case Ok(Void _) -> {
                String upn = jwt.getClaim("upn");
                String sid = jwt.getClaim("sid");
                var ids = byIdsAndFilters.ids();
                var idsQuery = ids.stream().map("id=%d"::formatted).collect(Collectors.joining("&"));
                var filters = byIdsAndFilters.filters();
                var filtersQuery = filters.stream().map("filters=%s"::formatted).collect(Collectors.joining("&"));
                var location = URI.create("/todos/search?%s&%s".formatted(idsQuery, filtersQuery));

                // todo: figure out how to update todos using only filters
                // ids.forEach(id -> {
                //    final var event = new SseEvent.TodoUpdated(upn, sid, id);
                //    eventBus.publish("sse.todo.updated", event);
                // });

                yield Response.seeOther(location)
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    public Uni<Response> deleteTodos(final Command.Todo.Delete.All all) {
        Uni<Result<Void, Error>> resultUni = executor.deleteTodos(all);

        return resultUni.map(result -> switch (result) {
            case Ok(_) -> {
                String upn = jwt.getClaim("upn");
                String sid = jwt.getClaim("sid");

                // todo: delete all todos in grid

                yield Response.ok()
                        .header("HX-Reswap", "none")
                        .header("HX-Trigger", "command.empty")
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    public Uni<Response> deleteTodos(final Command.Todo.Delete.ByIds byIds) {
        Uni<Result<Void, Error>> resultUni = executor.deleteTodos(byIds);

        return resultUni.map(result -> switch (result) {
            case Ok(_) -> {
                String upn = jwt.getClaim("upn");
                String sid = jwt.getClaim("sid");
                var ids = byIds.ids();

                ids.forEach(id -> {
                    var event = new SseEvent.TodoDeleted(upn, sid, id);
                    eventBus.publish("sse.todo.deleted", event);
                });

                var html = TodoTemplates.todosDelete(ids)
                        .render();

                yield Response.ok(html)
                        .header("HX-Reswap", "none")
                        .header("HX-Trigger", "command.empty")
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    public Uni<Response> showCalendar(final Command.Calendar.Show show) {
        Uni<Result<Void, Error>> resultUni = executor.showCalendar(show);

        return resultUni.map(result -> switch (result) {
            case Ok(_) -> {
                // todo: implement show calendar command
                yield Response.accepted()
                        .header("HX-Trigger", "command.empty")
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }

    public Uni<Response> showUser(final Command.User.Show show) {
        Uni<Result<Void, Error>> resultUni = executor.showUser(show);

        return resultUni.map(result -> switch (result) {
            case Ok(_) -> {
                // todo: implement show user command
                yield Response.accepted()
                        .header("HX-Trigger", "command.empty")
                        .build();
            }
            case Err(Error err) -> {
                var text = err.describe();
                var html = ErrorTemplates.error(text);

                yield Response.serverError()
                        .entity(html)
                        .build();
            }
        });
    }
}