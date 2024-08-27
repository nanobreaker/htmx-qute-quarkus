package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.usecases.v1.todo.command.TodoListCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

@ApplicationScoped
public class TodoListCommandHandler implements CommandHandler<TodoListCommand, Result<Void, Error>> {

    @Inject
    EventBus eventBus;

    @ConsumeEvent(value = "todo.list")
    @WithTransaction
    @WithSpan("handleTodoListCommand")
    @Override
    public Uni<Result<Void, Error>> handle(final TodoListCommand command) {
        return dispatchTodoListEvent(command)
                .map(Result::ok);
    }

    @WithSpan("dispatchTodoListEvent")
    public Uni<Void> dispatchTodoListEvent(final TodoListCommand command) {
        return Uni.createFrom()
                .item(() -> eventBus.publish("todo.listed", command))
                .replaceWithVoid();
    }
}