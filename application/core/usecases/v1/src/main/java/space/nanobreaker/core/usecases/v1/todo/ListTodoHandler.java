package space.nanobreaker.core.usecases.v1.todo;

import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.error.Error;

import java.util.Set;

import static space.nanobreaker.core.domain.v1.Query.Todo.List;

@ApplicationScoped
public class ListTodoHandler implements QueryHandler<List, Set<Todo>> {

    private final TodoRepository todoRepository;

    public ListTodoHandler(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @WithSpan("handleTodoGetCommand")
    @ConsumeEvent(value = "query.todo.list")
    @WithSession
    @Override
    public Uni<Result<Set<Todo>, Error>> execute(final List query) {
        return switch (query) {
            case List.All(var username) -> todoRepository.list(username);
            case List.ByIds(var ids) -> todoRepository.list(ids);
            case List.ByFilters(var username, var filters) -> todoRepository.list(username, filters);
            case List.ByIdsAndFilters(var ids, var filters) -> todoRepository.list(ids, filters);
        };
    }
}