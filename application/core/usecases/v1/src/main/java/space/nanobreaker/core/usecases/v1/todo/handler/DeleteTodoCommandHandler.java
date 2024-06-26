package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.TodoId;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.CommandHandler;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodoCommand;

@ApplicationScoped
public class DeleteTodoCommandHandler implements CommandHandler<DeleteTodoCommand, TodoId> {

    @Inject
    @Any
    TodoRepository todoRepository;

    @WithSpan("deleteTodoCommandHandler execute")
    @ConsumeEvent(value = "deleteTodoCommand")
    @WithTransaction
    public Uni<TodoId> execute(final DeleteTodoCommand deleteTodoCommand) {
        return todoRepository.deleteByTodoId(deleteTodoCommand.id())
                .replaceWith(deleteTodoCommand.id());
    }

}
