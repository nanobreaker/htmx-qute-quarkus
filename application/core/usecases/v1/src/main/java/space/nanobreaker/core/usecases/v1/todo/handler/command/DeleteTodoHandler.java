package space.nanobreaker.core.usecases.v1.todo.handler.command;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.todo.TodoEvent;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodo;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.ddd.EventDispatcher;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.result.Result;

import java.util.Set;

@ApplicationScoped
public class DeleteTodoHandler
        implements CommandHandler<DeleteTodo, Result<Void, Error>> {

    private final EventDispatcher eventDispatcher;
    private final TodoRepository todoRepository;

    public DeleteTodoHandler(
            EventDispatcher eventDispatcher,
            TodoRepository todoRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.todoRepository = todoRepository;
    }

    @ConsumeEvent(value = "todo.delete")
    @WithTransaction
    @WithSpan("handleTodoDeleteCommand")
    @Override
    public Uni<Result<Void, Error>> handle(final DeleteTodo command) {
        final TodoId id = command.id();

        return eventDispatcher.on(
                () -> todoRepository.delete(id),
                new TodoEvent.Deleted(Set.of(id))
        );
    }
}