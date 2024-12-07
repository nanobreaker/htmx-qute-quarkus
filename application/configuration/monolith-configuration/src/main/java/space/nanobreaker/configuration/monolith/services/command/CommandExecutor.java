package space.nanobreaker.configuration.monolith.services.command;

import io.github.dcadea.jresult.Result;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.Command.Todo.Create;
import space.nanobreaker.core.domain.v1.Command.Todo.Delete;
import space.nanobreaker.core.domain.v1.Command.Todo.Update;
import space.nanobreaker.core.domain.v1.Query;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.library.error.Error;

import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.dcadea.jresult.Result.empty;
import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class CommandExecutor {

    @Inject
    EventBus eventBus;

    @Inject
    SecurityIdentity securityIdentity;

    public Uni<Result<Todo, Error>> createTodo(
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

                yield eventBus
                        .<Result<Todo, Error>>request("command.todo.create", command)
                        .map(Message::body);
            }
            // todo: refactor, unreachable code
            case Command.Todo.Create.Help _ -> {
                yield Uni.createFrom()
                        .item(err(new CommandError.NotSupported()));
            }
        };
    }

    public Uni<Result<Set<Todo>, Error>> listTodo(final Command.Todo.List cmd) {
        return switch (cmd) {
            case Command.Todo.List.All _ -> {
                var username = securityIdentity.getPrincipal().getName();
                var query = new Query.Todo.List.All(username);

                yield eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);
            }
            case Command.Todo.List.ByIds(var ids) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var query = new Query.Todo.List.ByIds(idz);

                yield eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);
            }
            case Command.Todo.List.ByFilters(var filters) -> {
                var username = securityIdentity.getPrincipal().getName();
                var query = new Query.Todo.List.ByFilters(username, filters);

                yield eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);
            }
            case Command.Todo.List.ByIdsAndFilters(var ids, var filters) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var query = new Query.Todo.List.ByIdsAndFilters(idz, filters);

                yield eventBus
                        .<Result<Set<Todo>, Error>>request("query.todo.list", query)
                        .map(Message::body);
            }
            // todo: refactor, unreachable code
            case Command.Todo.List.Help _ -> {
                yield Uni.createFrom()
                        .item(err(new CommandError.NotSupported()));
            }
        };
    }

    public Uni<Result<Void, Error>> updateTodo(
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

                yield eventBus
                        .<Result<Void, Error>>request("command.todo.update", command)
                        .map(Message::body);
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

                yield eventBus
                        .<Result<Void, Error>>request("command.todo.update", command)
                        .map(Message::body);
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

                yield eventBus
                        .<Result<Void, Error>>request("command.todo.update", command)
                        .map(Message::body);
            }
            // todo: refactor, unreachable code
            case Command.Todo.Update.Help _ -> {
                yield Uni.createFrom()
                        .item(err(new CommandError.NotSupported()));
            }
        };
    }

    public Uni<Result<Void, Error>> deleteTodos(final Command.Todo.Delete cmd) {
        return switch (cmd) {
            case Command.Todo.Delete.All _ -> {
                var username = securityIdentity.getPrincipal().getName();
                var command = new Delete.All(username);

                yield eventBus
                        .<Result<Void, Error>>request("command.todo.delete", command)
                        .map(Message::body);
            }
            case Command.Todo.Delete.ByIds(var ids) -> {
                var username = securityIdentity.getPrincipal().getName();
                var idz = ids.stream().map(id -> new TodoId(id, username)).collect(Collectors.toSet());
                var command = new Delete.ByIds(idz);

                yield eventBus
                        .<Result<Void, Error>>request("command.todo.delete", command)
                        .map(Message::body);
            }
            case Command.Todo.Delete.Help _ -> {
                yield Uni.createFrom()
                        .item(err(new CommandError.NotSupported()));
            }
        };
    }

    public Uni<Result<Void, Error>> showCalendar(final Command.Calendar ignored) {
        // todo: Implement show calendar command
        return Uni.createFrom()
                .item(empty());
    }

    public Uni<Result<Void, Error>> showUser(final Command.User ignored) {
        // todo: Implement show user command
        return Uni.createFrom()
                .item(empty());
    }
}
