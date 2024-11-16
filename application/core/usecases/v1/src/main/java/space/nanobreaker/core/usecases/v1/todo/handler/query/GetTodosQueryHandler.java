package space.nanobreaker.core.usecases.v1.todo.handler.query;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodosQuery;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.either.Either;
import space.nanobreaker.library.either.Left;
import space.nanobreaker.library.either.Right;
import space.nanobreaker.library.error.Error;
import io.github.dcadea.jresult.Result;

import java.util.Set;

@ApplicationScoped
public class GetTodosQueryHandler
        implements QueryHandler<GetTodosQuery, Set<Todo>> {

    private final TodoRepository todoRepository;

    public GetTodosQueryHandler(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @ConsumeEvent(value = "todos.get")
    @WithSession
    @WithSpan("handleTodosGetCommand")
    @Override
    public Uni<Result<Set<Todo>, Error>> execute(final GetTodosQuery query) {
        final Either<Set<TodoId>, String> idsOrUsername = query.idsOrUsername();

        return switch (idsOrUsername) {
            case Left(var ids) -> todoRepository.list(ids);
            case Right(var username) -> todoRepository.list(username);
        };
    }
}
