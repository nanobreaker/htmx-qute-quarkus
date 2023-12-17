package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.QueryHandler;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodoQuery;

@ApplicationScoped
public class GetTodoQueryHandler implements QueryHandler<GetTodoQuery, Todo> {

    @Inject
    @Any
    TodoRepository todoRepository;

    @Override
    @WithSpan("getTodoQueryHandler execute")
    @ConsumeEvent(value = "getTodoQuery")
    @WithSession
    public Uni<Todo> execute(GetTodoQuery query) {
        return todoRepository.findByTodoId(query.id());
    }
}
