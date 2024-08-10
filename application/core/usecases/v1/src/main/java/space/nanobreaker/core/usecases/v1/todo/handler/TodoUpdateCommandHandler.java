package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.command.TodoUpdateCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

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
        final String username = command.username();
        final Set<String> filters = command.filters();
        final Option<String> title = command.title();
        final Option<String> description = command.description();
        final Option<LocalDateTime> start = command.start();
        final Option<LocalDateTime> end = command.end();

        final Multi<Todo> todosToUpdate = todoRepository
                .listBy(username, filters)
                .onItem().transformToMulti(todos -> Multi.createFrom().items(todos));

        final Multi<TodoId> updatedTodosIds = todosToUpdate
                .map(todo -> todoRepository
                        .update(
                                todo,
                                title,
                                description,
                                start,
                                end
                        )
                        .map(result -> todo.getId())
                )
                .onItem().transformToUniAndMerge(resultUni -> resultUni);

        return updatedTodosIds
                .invoke(this::dispatchTodoUpdateEvent)
                .collect().with(Collectors.counting())
                .replaceWith(() -> Result.ok(null));
    }

    @WithSpan("dispatchTodoUpdatedEvent")
    public void dispatchTodoUpdateEvent(final TodoId todoId) {
        switch (Option.over(todoId)) {
            case Some(final TodoId t) -> eventBus.publish("todo.updated", t);
            case None<TodoId> ignored -> Log.error("todo.updated ignored");
        }
    }

}