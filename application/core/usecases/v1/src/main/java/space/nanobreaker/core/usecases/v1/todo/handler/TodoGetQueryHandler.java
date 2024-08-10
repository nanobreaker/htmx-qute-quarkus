package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.query.TodoGetQuery;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.None;
import space.nanobreaker.library.Result;
import space.nanobreaker.library.Some;

@ApplicationScoped
public class TodoGetQueryHandler implements QueryHandler<TodoGetQuery, Todo> {

    @Inject
    TodoRepository todoRepository;

    @ConsumeEvent(value = "todo.get")
    @WithSession
    @WithSpan("handleTodoGetCommand")
    @Override
    public Uni<Result<Todo, Error>> execute(final TodoGetQuery query) {
        final TodoId id = query.id();
        return todoRepository.findById(id)
                .map(todoOption -> switch (todoOption) {
                    case Some(final Todo todo) -> Result.ok(todo);
                    case None<Todo> ignored -> Result.err(null);
                });
    }
}