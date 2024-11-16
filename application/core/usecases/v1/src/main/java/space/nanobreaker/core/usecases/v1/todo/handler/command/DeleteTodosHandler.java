package space.nanobreaker.core.usecases.v1.todo.handler.command;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.todo.TodoEvent;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodos;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.ddd.EventDispatcher;
import space.nanobreaker.library.error.Error;
import io.github.dcadea.jresult.Result;

import java.util.Set;

@ApplicationScoped
public class DeleteTodosHandler
        implements CommandHandler<DeleteTodos, Result<Void, Error>> {

    private final EventDispatcher eventDispatcher;
    private final TodoRepository todoRepository;

    public DeleteTodosHandler(
            final EventDispatcher eventDispatcher,
            final TodoRepository todoRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.todoRepository = todoRepository;
    }

    @ConsumeEvent(value = "todos.delete")
    @WithTransaction
    @WithSpan("handleTodoDeleteCommand")
    @Override
    public Uni<Result<Void, Error>> handle(final DeleteTodos command) {
        final Set<TodoId> ids = command.ids();

        return eventDispatcher.on(
                () -> todoRepository.delete(ids),
                new TodoEvent.Deleted(ids)
        );
    }
}