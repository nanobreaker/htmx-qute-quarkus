package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    @ConsumeEvent(value = "todo.create")
    @WithSpan("handleTodoCreateCommand")
    @WithSession
    public Uni<Result<Void, Error>> handle(final TodoCreateCommand todoCreateCommand) {
        final TodoId id = new TodoId(UUID.randomUUID(), todoCreateCommand.username());
        final Todo todo = new Todo(
                id,
                todoCreateCommand.title(),
                todoCreateCommand.description(),
                TodoState.ACTIVE,
                todoCreateCommand.start(),
                todoCreateCommand.end()
        );

        return todoRepository.save(todo)
                .invoke(this::dispatchTodoCreatedEvent)
                .map(result -> switch (result) {
                    case Ok(Todo _) -> Result.ok(null);
                    case Err(Error e) -> Result.err(e);
                });
    }

    @WithSpan("dispatchTodoCreatedEvent")
    public void dispatchTodoCreatedEvent(final Result<Todo, Error> result) {
        switch (result) {
            case Ok(Todo t) -> eventBus.publish("todo.created", t);
            case Err(Error _) -> Log.error("failed to save todo");
        }
    }

}