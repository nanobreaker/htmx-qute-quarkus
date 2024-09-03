package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.TodoErr;
import space.nanobreaker.core.usecases.v1.todo.command.TodoUpdateCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Option;
import space.nanobreaker.library.Result;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class TodoUpdateCommandHandler implements CommandHandler<TodoUpdateCommand, Result<Void, Error>> {

    @Inject
    TodoRepository todoRepository;

    @Inject
    EventBus eventBus;

    @ConsumeEvent(value = "todo.update")
    @WithSpan("handleTodoUpdateCommand")
    @WithTransaction
    public Uni<Result<Void, Error>> handle(final TodoUpdateCommand command) {
        final Option<Set<TodoId>> ids = command.ids();
        final Option<List<String>> filters = command.filters();

        if (ids.isNone() && filters.isNone())
            return Uni.createFrom()
                    .item(Result.err(new TodoErr.ArgumentOrOptionRequired()));

        final Multi<Todo> todosToUpdate = todoRepository
                .listBy(command.username(), ids, filters)
                .onItem().transformToMulti(todos -> Multi.createFrom().items(todos));

        final Multi<TodoId> updatedTodosIds = todosToUpdate
                .map(todo -> todoRepository
                        .update(
                                todo,
                                command.title(),
                                command.description(),
                                command.start(),
                                command.end()
                        )
                        .map(result -> todo.getId())
                )
                .onItem().transformToUniAndMerge(resultUni -> resultUni);

        return updatedTodosIds
                .map(this::dispatchTodoUpdateEvent)
                .onItem().transformToUniAndMerge(voidUni -> voidUni)
                .toUni()
                .replaceWith(() -> Result.ok(null));
    }

    @WithSpan("dispatchTodoUpdatedEvent")
    public Uni<Void> dispatchTodoUpdateEvent(final TodoId todoId) {
        return Uni.createFrom()
                .item(() -> eventBus.publish("todo.updated", todoId))
                .replaceWithVoid();
    }
}