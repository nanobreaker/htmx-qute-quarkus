package space.nanobreaker.core.usecases.v1.todo.handler.query;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.TodoError;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodoQuery;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.error.None;
import space.nanobreaker.library.result.Result;

@ApplicationScoped
public class GetTodoHandler implements QueryHandler<GetTodoQuery, Todo> {

    private final TodoRepository todoRepository;

    public GetTodoHandler(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @ConsumeEvent(value = "todo.get")
    @WithSession
    @WithSpan("handleTodoGetCommand")
    @Override
    public Uni<Result<Todo, Error>> execute(final GetTodoQuery query) {
        final TodoId id = query.id();

        return todoRepository.find(id)
                .map(result -> result
                        .mapErr(e -> switch (e) {
                            case None() -> new TodoError.NotFound();
                            default -> new TodoError.Unknown();
                        })
                );
    }
}