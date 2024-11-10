package space.nanobreaker.core.usecases.v1.todo.handler.command;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoEvent;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.command.UpdateTodo;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.ddd.EventDispatcher;
import space.nanobreaker.library.either.Either;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.result.Err;
import space.nanobreaker.library.result.Ok;
import space.nanobreaker.library.result.Result;

@ApplicationScoped
public class UpdateTodoHandler implements CommandHandler<UpdateTodo, Result<Void, Error>> {

    private final EventDispatcher eventDispatcher;
    private final TodoRepository todoRepository;

    public UpdateTodoHandler(
            EventDispatcher eventDispatcher,
            TodoRepository todoRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.todoRepository = todoRepository;
    }

    @ConsumeEvent(value = "todo.update")
    @WithSpan("handleTodoUpdateCommand")
    @WithSession
    @Override
    public Uni<Result<Void, Error>> handle(final UpdateTodo command) {
        final Either<String, Set<TodoId>> usernameOrIds = command.usernameOrIds();
        final Option<List<String>> filters = command.filters();

        final Uni<Result<Set<Todo>, Error>> todos = todoRepository.list(usernameOrIds, filters);

        return todos.flatMap(result ->
                switch (result) {
                    case Ok(Set<Todo> todoSet) -> {
                        final var ids = todoSet
                                .stream()
                                .map(Todo::getId)
                                .collect(Collectors.toUnmodifiableSet());

                        yield eventDispatcher.on(
                                () ->
                                        todoRepository.update(
                                                todoSet,
                                                command.title(),
                                                command.description(),
                                                command.start(),
                                                command.end()
                                        ),
                                new TodoEvent.Updated(ids)
                        );
                    }
                    case Err(Error error) -> {
                        yield Uni.createFrom().item(Result.err(error));
                    }
                }
        );
    }
}
