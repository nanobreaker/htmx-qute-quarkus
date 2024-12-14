package space.nanobreaker.configuration.monolith.services.command;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import space.nanobreaker.configuration.monolith.services.sse.SseEvent;
import space.nanobreaker.configuration.monolith.templates.ErrorTemplates;
import space.nanobreaker.configuration.monolith.templates.HelpTemplates;
import space.nanobreaker.configuration.monolith.templates.TodoTemplates;
import space.nanobreaker.core.domain.v1.Command.Todo.Create;
import space.nanobreaker.core.domain.v1.Command.Todo.Delete;
import space.nanobreaker.core.domain.v1.Command.Todo.Update;
import space.nanobreaker.core.domain.v1.Query;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.library.error.Error;

import java.net.URI;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommandExecutor {

    @Inject
    EventBus eventBus;

    @Inject
    CommandDescriber describer;

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
            final Command.Todo.Create cmd,
            final ZoneId zoneId
    ) {
        return switch (cmd) {
            case Command.Todo.Create.Default(
                    var title,
                    var description,
                    var start,
                    var end
            ) -> {
                var username = securityIdentity.getPrincipal().getName();
                var startZoned = start.map(s -> s.atZone(zoneId));
                var endZoned = end.map(e -> e.atZone(zoneId));
                var command = new Create(username, title, description, startZoned, endZoned);
                var responseUni = eventBus
                        .<Result<Todo, Error>>request("command.todo.create", command)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
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
            case Command.Todo.Create.Help help -> {
                var text = describer.describe(help);
                var html = HelpTemplates.help(text);
                var response = Response.ok()
                        .header("HX-Retarget", "#feedback")
                        .entity(html)
                        .build();

                yield Uni.createFrom().item(response);
            }
        };
    }

    public Uni<Response> listTodo(
            final Command.Todo.List cmd,
            final ZoneId zoneId
    ) {
        return switch (cmd) {
            case Command.Todo.List.All _ -> {
                var username = securityIdentity.getPrincipal().getName();
                var query = new Query.Todo.List.All(username);
                var responseUni = eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
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
            case Command.Todo.List.ByIds(var ids) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var query = new Query.Todo.List.ByIds(idz);

                var responseUni = eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
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
            case Command.Todo.List.ByFilters(var filters) -> {
                var username = securityIdentity.getPrincipal().getName();
                var query = new Query.Todo.List.ByFilters(username, filters);

                var responseUni = eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
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
            case Command.Todo.List.ByIdsAndFilters(var ids, var filters) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var query = new Query.Todo.List.ByIdsAndFilters(idz, filters);

                var responseUni = eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
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
            case Command.Todo.List.Help help -> {
                var text = describer.describe(help);
                var html = HelpTemplates.help(text);
                var response = Response.ok()
                        .header("HX-Retarget", "#feedback")
                        .entity(html)
                        .build();

                yield Uni.createFrom().item(response);
            }
        };
    }

    public Uni<Response> updateTodo(
            final Command.Todo.Update cmd,
            final ZoneId zoneId
    ) {
        return switch (cmd) {
            case Command.Todo.Update.ByIds(var ids, var payload) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var payloadz = new Update.Payload(
                        payload.title(),
                        payload.description(),
                        payload.start().map(d -> d.atZone(zoneId)),
                        payload.end().map(d -> d.atZone(zoneId))
                );
                var command = new Update.ByIds(idz, payloadz);
                var responseUni = eventBus
                        .<Result<Void, Error>>request("command.todo.update", command)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
                    case Ok(Void _) -> {
                        String upn = jwt.getClaim("upn");
                        String sid = jwt.getClaim("sid");
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
            case Command.Todo.Update.ByFilters(var filters, var payload) -> {
                var username = securityIdentity.getPrincipal().getName();
                var payloadz = new Update.Payload(
                        payload.title(),
                        payload.description(),
                        payload.start().map(d -> d.atZone(zoneId)),
                        payload.end().map(d -> d.atZone(zoneId))
                );
                var command = new Update.ByFilters(username, filters, payloadz);

                var responseUni = eventBus
                        .<Result<Void, Error>>request("command.todo.update", command)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
                    case Ok(Void _) -> {
                        String upn = jwt.getClaim("upn");
                        String sid = jwt.getClaim("sid");
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
            case Command.Todo.Update.ByIdsAndFilters(var ids, var filters, var payload) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var payloadz = new Update.Payload(
                        payload.title(),
                        payload.description(),
                        payload.start().map(d -> d.atZone(zoneId)),
                        payload.end().map(d -> d.atZone(zoneId))
                );
                var command = new Update.ByIdsAndFilters(idz, filters, payloadz);

                var responseUni = eventBus
                        .<Result<Void, Error>>request("command.todo.update", command)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
                    case Ok(Void _) -> {
                        String upn = jwt.getClaim("upn");
                        String sid = jwt.getClaim("sid");
                        var idsQuery = ids.stream().map("id=%d"::formatted).collect(Collectors.joining("&"));
                        var filtersQuery = filters.stream().map("filters=%s"::formatted).collect(Collectors.joining("&"));
                        var location = URI.create("/todos/search?%s&%s".formatted(idsQuery, filtersQuery));

                        // todo: figure out how to update todos using ids and filters
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
            case Command.Todo.Update.Help help -> {
                var text = describer.describe(help);
                var html = HelpTemplates.help(text);
                var response = Response.ok()
                        .header("HX-Retarget", "#feedback")
                        .entity(html)
                        .build();

                yield Uni.createFrom().item(response);
            }
        };
    }

    public Uni<Response> deleteTodos(final Command.Todo.Delete cmd) {
        return switch (cmd) {
            case Command.Todo.Delete.All _ -> {
                var username = securityIdentity.getPrincipal().getName();
                var command = new Delete.All(username);
                var responseUni = eventBus
                        .<Result<Void, Error>>request("command.todo.delete", command)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
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
            case Command.Todo.Delete.ByIds(var ids) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var command = new Delete.ByIds(idz);
                var responseUni = eventBus
                        .<Result<Void, Error>>request("command.todo.delete", command)
                        .map(Message::body);

                yield responseUni.map(result -> switch (result) {
                    case Ok(_) -> {
                        String upn = jwt.getClaim("upn");
                        String sid = jwt.getClaim("sid");

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
            case Command.Todo.Delete.Help help -> {
                var text = describer.describe(help);
                var html = HelpTemplates.help(text);
                var response = Response.ok()
                        .header("HX-Retarget", "#feedback")
                        .entity(html)
                        .build();

                yield Uni.createFrom().item(response);
            }
        };
    }

    public Uni<Response> showCalendar(final Command.Calendar ignored) {
        // todo: Implement show calendar command
        return Uni.createFrom()
                .item(Response.serverError().build());
    }

    public Uni<Response> showUser(final Command.User ignored) {
        // todo: Implement show user command
        return Uni.createFrom()
                .item(Response.serverError().build());
    }
}
