package dev.thatwhichis.app.usecases.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoEvent;
import dev.thatwhichis.core.ports.inbound.todo.TodoCommand;
import dev.thatwhichis.core.ports.outbound.todo.TodoRepository;
import dev.thatwhichis.framework.cqrs.CommandHandler;
import dev.thatwhichis.framework.ddd.DomainEvent;
import dev.thatwhichis.framework.ddd.EventDispatcher;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class UpdateTodoHandler implements CommandHandler<TodoCommand.Update, Void> {

    private final EventDispatcher eventDispatcher;
    private final TodoRepository todoRepository;

    @Inject
    public UpdateTodoHandler(
            final EventDispatcher eventDispatcher,
            final TodoRepository todoRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.todoRepository = todoRepository;
    }

    @Override
    @ConsumeEvent(value = "command.todo.update")
    @WithSession
    @WithSpan("handleTodoUpdateCommand")
    public Uni<Result<Void, Error>> handle(final TodoCommand.Update command) {
        return switch (command) {
            case TodoCommand.Update.ByIds(var ids, var payload) -> {
                var resultUni = todoRepository.list(ids);

                yield resultUni.flatMap(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        var domainEvents = todos.stream()
                                .map(TodoEvent.Updated::new)
                                .collect(Collectors.<DomainEvent>toUnmodifiableList());

                        yield eventDispatcher.on(() -> todoRepository.update(todos, payload), domainEvents);
                    }
                    case Err(Error error) -> {
                        yield Uni.createFrom()
                                .item(Result.err(error));
                    }
                });
            }
            case TodoCommand.Update.ByFilters(var username, var filters, var payload) -> {
                var resultUni = todoRepository.list(username, filters);

                yield resultUni.flatMap(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        var domainEvents = todos.stream()
                                .map(TodoEvent.Updated::new)
                                .collect(Collectors.<DomainEvent>toUnmodifiableList());

                        yield eventDispatcher.on(() -> todoRepository.update(todos, payload), domainEvents);
                    }
                    case Err(Error error) -> {
                        yield Uni.createFrom()
                                .item(Result.err(error));
                    }
                });
            }
            case TodoCommand.Update.ByIdsAndFilters(var ids, var filters, var payload) -> {
                var resultUni = todoRepository.list(ids, filters);

                yield resultUni.flatMap(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        var domainEvents = todos.stream()
                                .map(TodoEvent.Updated::new)
                                .collect(Collectors.<DomainEvent>toUnmodifiableList());

                        yield eventDispatcher.on(() -> todoRepository.update(todos, payload), domainEvents);
                    }
                    case Err(Error error) -> {
                        yield Uni.createFrom()
                                .item(Result.err(error));
                    }
                });
            }
        };
    }
}
