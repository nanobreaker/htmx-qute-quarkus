package space.nanobreaker.core.usecases.v1.todo.handler.command;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.command.TodoDeleteCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

@ApplicationScoped
public class TodoDeleteCommandHandler implements CommandHandler<TodoDeleteCommand, Result<Void, Error>> {

    @Inject
    TodoRepository todoRepository;

    @ConsumeEvent(value = "todo.delete")
    @WithTransaction
    @WithSpan("handleTodoDeleteCommand")
    @Override
    public Uni<Result<Void, Error>> handle(final TodoDeleteCommand command) {
        final TodoId id = command.id();

        return todoRepository.deleteByTodoId(id)
                .map(Result::ok);
    }
}