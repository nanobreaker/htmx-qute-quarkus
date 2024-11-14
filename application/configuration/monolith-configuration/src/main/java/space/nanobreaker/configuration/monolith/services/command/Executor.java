package space.nanobreaker.configuration.monolith.services.command;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.usecases.v1.todo.command.CreateTodo;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodos;
import space.nanobreaker.core.usecases.v1.todo.command.UpdateTodo;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodosQuery;
import space.nanobreaker.library.either.Either;
import space.nanobreaker.library.either.Left;
import space.nanobreaker.library.either.Right;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.None;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.option.Some;
import space.nanobreaker.library.result.Result;

import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class Executor {

    @Inject
    EventBus eventBus;

    @Inject
    SecurityIdentity securityIdentity;

    public Uni<Result<Todo, Error>> createTodo(
            final CreateTodoCommand cmd,
            final ZoneId zoneId
    ) {
        final var start = cmd.start().map(s -> s.atZone(zoneId));
        final var end = cmd.end().map(e -> e.atZone(zoneId));
        final CreateTodo command = new CreateTodo(
                securityIdentity.getPrincipal().getName(),
                cmd.title(),
                cmd.description(),
                start,
                end
        );

        return eventBus
                .<Result<Todo, Error>>request("todo.create", command)
                .map(Message::body);
    }

    public Uni<Result<Void, Error>> updateTodo(
            final UpdateTodoCommand cmd,
            final ZoneId zoneId
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        final Option<Set<TodoId>> idsOption = cmd
                .ids()
                .map(ids -> ids
                        .stream()
                        .map(id -> new TodoId(id, username))
                        .collect(Collectors.toSet())
                );
        final Either<String, Set<TodoId>> usernameOrIds = switch (idsOption) {
            case Some(Set<TodoId> ids) -> new Right<>(ids);
            case None() -> new Left<>(username);
        };
        final var start = cmd.start().map(s -> s.atZone(zoneId));
        final var end = cmd.end().map(e -> e.atZone(zoneId));
        final UpdateTodo updateTodo = new UpdateTodo(
                usernameOrIds,
                cmd.filters(),
                cmd.title(),
                cmd.description(),
                start,
                end
        );

        return eventBus
                .<Result<Void, Error>>request("todo.update", updateTodo)
                .map(Message::body);
    }

    public Uni<Result<Set<Todo>, Error>> listTodo(final ListTodoCommand cmd) {
        final String username = securityIdentity.getPrincipal().getName();
        final Either<Set<TodoId>, String> usernameOrIds = switch (cmd.ids()) {
            case Some(Set<Integer> ids) -> new Left<>(ids
                    .stream()
                    .map(id -> new TodoId(id, username))
                    .collect(Collectors.toUnmodifiableSet())
            );
            case None() -> new Right<>(username);
        };
        final GetTodosQuery todoListCommand = new GetTodosQuery(usernameOrIds);

        return eventBus
                .<Result<Set<Todo>, Error>>request("todos.get", todoListCommand)
                .map(Message::body);
    }

    public Uni<Result<Void, Error>> deleteTodos(final DeleteTodoCommand cmd) {
        final String username = securityIdentity.getPrincipal().getName();
        final Set<TodoId> ids = cmd
                .ids()
                .stream()
                .map(id -> new TodoId(id, username))
                .collect(Collectors.toSet());

        final var request = new DeleteTodos(ids);
        return eventBus
                .<Result<Void, Error>>request("todos.delete", request)
                .map(Message::body);
    }

    public Uni<Result<Void, Error>> showCalendar(final ShowCalendarCommand ignored) {
        // TODO: Implement show calendar command
        return Uni.createFrom()
                .item(Result.okVoid());
    }

    public Uni<Result<Void, Error>> showUser(final ShowUserCommand ignored) {
        // TODO: Implement show user command
        return Uni.createFrom()
                .item(Result.okVoid());
    }
}
