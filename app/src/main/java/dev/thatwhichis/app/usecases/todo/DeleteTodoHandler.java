package dev.thatwhichis.app.usecases.todo;

import dev.thatwhichis.core.domain.todo.TodoEvent;
import dev.thatwhichis.core.ports.inbound.todo.TodoCommand;
import dev.thatwhichis.core.ports.outbound.todo.TodoRepository;
import dev.thatwhichis.framework.cqrs.CommandHandler;
import dev.thatwhichis.framework.ddd.DomainEvent;
import dev.thatwhichis.framework.ddd.EventDispatcher;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class DeleteTodoHandler implements CommandHandler<TodoCommand.Delete, Void> {

    private final EventDispatcher eventDispatcher;
    private final TodoRepository todoRepository;

    @Inject
    public DeleteTodoHandler(
            final EventDispatcher eventDispatcher,
            final TodoRepository todoRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.todoRepository = todoRepository;
    }

    @Override
    @ConsumeEvent(value = "command.todo.delete")
    @WithTransaction
    @WithSpan("handleTodoDeleteCommand")
    public Uni<Result<Void, Error>> handle(final TodoCommand.Delete command) {
        return switch (command) {
            case TodoCommand.Delete.All(var username) -> {
                var domainEvents = Set.<DomainEvent>of(new TodoEvent.DeletedAll());

                yield eventDispatcher.on(() -> todoRepository.deleteAll(username), domainEvents);
            }
            case TodoCommand.Delete.ByIds(var ids) -> {
                var domainEvents = ids.stream()
                        .map(TodoEvent.Deleted::new)
                        .collect(Collectors.<DomainEvent>toUnmodifiableList());

                yield eventDispatcher.on(() -> todoRepository.delete(ids), domainEvents);
            }
        };
    }
}