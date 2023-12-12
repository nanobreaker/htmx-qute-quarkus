package space.nanobreaker.core.usecases.v1.todo.handler;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.QueryHandler;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodosQuery;

import java.util.List;

@ApplicationScoped
public class GetTodosQueryHandler implements QueryHandler<
        GetTodosQuery, List<Todo>> {

    @Inject
    @Any
    TodoRepository todoRepository;

    @Override
    @ConsumeEvent(value = "getTodosQuery")
    @WithSession
    public Uni<List<Todo>> execute(GetTodosQuery query) {
        return todoRepository.listAllTodos();
    }
}

