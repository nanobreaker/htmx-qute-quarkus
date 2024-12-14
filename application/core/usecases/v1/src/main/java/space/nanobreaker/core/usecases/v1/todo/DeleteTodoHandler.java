package space.nanobreaker.core.usecases.v1.todo;

import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.Command.Todo.Delete;
import space.nanobreaker.core.domain.v1.todo.TodoEvent;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.ddd.EventDispatcher;
import space.nanobreaker.library.error.Error;

import java.util.Set;

@ApplicationScoped
public class DeleteTodoHandler
        implements CommandHandler<Delete, Result<Void, Error>> {

    private final EventDispatcher eventDispatcher;
    private final TodoRepository todoRepository;

    public DeleteTodoHandler(
            final EventDispatcher eventDispatcher,
            final TodoRepository todoRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.todoRepository = todoRepository;
    }

    @Override
    @ConsumeEvent(value = "command.todo.delete")
    @WithSpan("handleTodoDeleteCommand")
    @WithTransaction
    public Uni<Result<Void, Error>> handle(final Delete command) {
        return switch (command) {
            case Delete.All(var username) -> {
                yield eventDispatcher.on(
                        () -> todoRepository.deleteAll(username),
                        new TodoEvent.Deleted(Set.of())
                );
            }
            case Delete.ByIds(var ids) -> {
                yield eventDispatcher.on(
                        () -> todoRepository.delete(ids),
                        new TodoEvent.Deleted(ids)
                );
            }
        };
    }
}