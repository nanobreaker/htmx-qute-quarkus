package dev.thatwhichis.app.usecases.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.ports.inbound.todo.TodoQuery;
import dev.thatwhichis.core.ports.outbound.todo.TodoRepository;
import dev.thatwhichis.framework.cqrs.QueryHandler;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;

@ApplicationScoped
public class ListTodoHandler implements QueryHandler<TodoQuery.List, Set<Todo>> {

    private final TodoRepository todoRepository;

    @Inject
    public ListTodoHandler(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    @ConsumeEvent(value = "query.todo.list")
    @WithSession
    @WithSpan("handleTodoListQuery")
    public Uni<Result<Set<Todo>, Error>> execute(final TodoQuery.List query) {
        return switch (query) {
            case TodoQuery.List.All(var userId) -> todoRepository.list(userId);
            case TodoQuery.List.ByIds(var ids) -> todoRepository.list(ids);
            case TodoQuery.List.ByFilters(var userId, var filters) -> todoRepository.list(userId, filters);
            case TodoQuery.List.ByIdsAndFilters(var ids, var filters) -> todoRepository.list(ids, filters);
        };
    }
}