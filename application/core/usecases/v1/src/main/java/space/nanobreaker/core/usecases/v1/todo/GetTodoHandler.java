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

import static space.nanobreaker.core.domain.v1.Query.Todo.Get;

@ApplicationScoped
public class GetTodoHandler implements QueryHandler<Get, Todo> {

    private final TodoRepository todoRepository;

    public GetTodoHandler(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    @ConsumeEvent(value = "query.todo.get")
    @WithSpan("handleTodoGetCommand")
    @WithSession
    public Uni<Result<Todo, Error>> execute(final Get query) {
        return switch (query) {
            case Get.ById(var id) -> todoRepository.get(id);
        };
    }
}