package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.query.TodoGetQuery;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Option;
import space.nanobreaker.library.Result;

@ApplicationScoped
public class TodoGetQueryHandler implements QueryHandler<TodoGetQuery, Option<Todo>> {

    @Inject
    TodoRepository todoRepository;

    @Override
    @WithSpan("getTodoQueryHandler execute")
    @ConsumeEvent(value = "getTodoQuery")
    public Result<Option<Todo>, Error> execute(TodoGetQuery query) {
        return todoRepository.findByTodoId(query.id());
    }
}
