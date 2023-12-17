package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.CommandHandler;
import space.nanobreaker.core.usecases.v1.todo.command.CreateTodoCommand;

import java.util.UUID;

@ApplicationScoped
public class CreateTodoCommandHandler implements CommandHandler<CreateTodoCommand, UUID> {

    @Inject
    @Any
    TodoRepository todoRepository;

    @WithSpan("createTodoCommandHandler execute")
    @ConsumeEvent(value = "createTodoCommand")
    @WithTransaction
    public Uni<UUID> execute(final CreateTodoCommand createTodoCommand) {
        final Todo todo = mapCreateTodoRequestToTodoModel(createTodoCommand);
        return todoRepository
                .persist(todo)
                .map(Todo::getId);
    }

    private Todo mapCreateTodoRequestToTodoModel(final CreateTodoCommand createTodoCommand) {
        return Todo.builder()
                .title(createTodoCommand.title())
                .description(createTodoCommand.description())
                .target(createTodoCommand.target())
                .completed(false)
                .build();
    }

}
