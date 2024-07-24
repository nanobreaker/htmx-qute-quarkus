package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.domain.v1.todo.TodoState;
import space.nanobreaker.core.usecases.v1.todo.command.TodoCreateCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Err;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Ok;
import space.nanobreaker.library.Result;

import java.util.UUID;

@ApplicationScoped
public class TodoCreateCommandHandler implements CommandHandler<TodoCreateCommand> {

    @Inject
    TodoRepository todoRepository;

    @Inject
    EventBus eventBus;

    @Transactional
    @ConsumeEvent(value = "todo.create")
    @WithSpan("createTodoCommandHandler execute")
    public Result<Void, Error> execute(final TodoCreateCommand todoCreateCommand) {
        final TodoId id = new TodoId(UUID.randomUUID(), todoCreateCommand.username());
        final Todo todo = new Todo(
                id,
                todoCreateCommand.title(),
                todoCreateCommand.description(),
                TodoState.ACTIVE,
                todoCreateCommand.start(),
                todoCreateCommand.end()
        );

        final Result<Todo, Error> result = todoRepository.save(todo);

        return switch (result) {
            case Ok(Todo t) -> {
                eventBus.publish("todo.created", t);
                yield Result.ok(null);
            }
            case Err(Error e) -> Result.err(e);
        };
    }

}