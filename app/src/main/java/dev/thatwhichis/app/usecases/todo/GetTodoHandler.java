package dev.thatwhichis.app.usecases.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoError;
import dev.thatwhichis.core.ports.inbound.todo.TodoQuery;
import dev.thatwhichis.core.ports.outbound.todo.TodoRepository;
import dev.thatwhichis.framework.cqrs.QueryHandler;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.None;
import dev.thatwhichis.library.option.Some;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static io.github.dcadea.jresult.Result.err;
import static io.github.dcadea.jresult.Result.ok;

@ApplicationScoped
public class GetTodoHandler implements QueryHandler<TodoQuery.Get, Todo> {

    private final TodoRepository todoRepository;

    @Inject
    public GetTodoHandler(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    @ConsumeEvent(value = "query.todo.get")
    @WithSession
    @WithSpan("handleTodoGetQuery")
    public Uni<Result<Todo, Error>> execute(final TodoQuery.Get query) {
        return switch (query) {
            case TodoQuery.Get.ById(var id) -> todoRepository
                    .find(id)
                    .map(result -> result.andThen(opt -> switch (opt) {
                        case Some(var todo) -> ok(todo);
                        case None() -> err(new TodoError.NotFound());
                    }));
        };
    }
}