package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.query.TodosGetQuery;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.List;

@ApplicationScoped
public class TodosGetQueryHandler implements QueryHandler<TodosGetQuery, List<Todo>> {

    @Inject
    TodoRepository todoRepository;

    @Override
    @WithSpan("getTodosQueryHandler execute")
    @ConsumeEvent(value = "getTodosQuery")
    @Transactional
    public Result<List<Todo>, Error> execute(TodosGetQuery query) {
        return todoRepository.listTodos();
    }
}

