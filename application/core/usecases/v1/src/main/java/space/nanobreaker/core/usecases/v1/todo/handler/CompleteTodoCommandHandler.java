package space.nanobreaker.core.usecases.v1.todo.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.CommandHandler;
import space.nanobreaker.core.usecases.v1.todo.command.CompleteTodoCommand;

import java.util.UUID;

@ApplicationScoped
public class CompleteTodoCommandHandler implements CommandHandler<CompleteTodoCommand, UUID> {

    @Inject
    @Any
    TodoRepository todoRepository;

    @ConsumeEvent(value = "completeTodoCommand")
    @WithTransaction
    public Uni<UUID> execute(final CompleteTodoCommand completeTodoCommand) {
        return todoRepository.complete(completeTodoCommand.id())
                .replaceWith(completeTodoCommand.id());
    }

}
