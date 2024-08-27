package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.query.TodosGetQuery;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class TodosGetQueryHandler implements QueryHandler<TodosGetQuery, Set<Todo>> {

    @Inject
    TodoRepository todoRepository;

    @ConsumeEvent(value = "todos.get")
    @WithSession
    @WithSpan("handleTodosGetCommand")
    @Override
    public Uni<Result<Set<Todo>, Error>> execute(final TodosGetQuery query) {
        // todo: add handling to list by ids
        final Set<Integer> ids = query.ids();

        return todoRepository.list(query.username())
                .map(todoStream -> todoStream.collect(Collectors.toSet()))
                .map(Result::ok);
    }
}