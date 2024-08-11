package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.*;
import space.nanobreaker.core.usecases.v1.todo.command.TodoCreateCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

@ApplicationScoped
public class TodoCreateCommandHandler implements CommandHandler<TodoCreateCommand, Result<TodoId, Error>> {

    @Inject
    TodoRepository todoRepository;

    @Inject
    TodoIdSequenceGenerator idGenerator;

    @Inject
    EventBus eventBus;

    @ConsumeEvent(value = "todo.create")
    @WithSpan("handleTodoCreateCommand")
    @WithTransaction
    public Uni<Result<TodoId, Error>> handle(final TodoCreateCommand todoCreateCommand) {
        final Uni<TodoId> nextId = idGenerator.next(todoCreateCommand.username());

        final Uni<Todo> createdTodo = nextId
                .map(todoId -> new Todo(
                        todoId,
                        todoCreateCommand.title(),
                        todoCreateCommand.description(),
                        TodoState.ACTIVE,
                        todoCreateCommand.start(),
                        todoCreateCommand.end()
                ))
                .chain(todoRepository::save);

        return createdTodo
                .flatMap(todo -> this.dispatchTodoCreatedEvent(todo).replaceWith(todo))
                .flatMap(todo -> idGenerator.increment(todo.getId().getUsername()).replaceWith(todo))
                .map(todo -> Result.ok(todo.getId()));
    }

    @WithSpan("dispatchTodoCreatedEvent")
    public Uni<Void> dispatchTodoCreatedEvent(final Todo result) {
        return Uni.createFrom()
                .item(() -> eventBus.publish("todo.created", result))
                .replaceWithVoid();
    }

}