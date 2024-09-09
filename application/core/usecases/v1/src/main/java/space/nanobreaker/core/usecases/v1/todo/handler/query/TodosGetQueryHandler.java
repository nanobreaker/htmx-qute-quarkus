package space.nanobreaker.core.usecases.v1.todo.handler.query;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.query.TodosGetQuery;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.*;
import space.nanobreaker.library.Error;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class TodosGetQueryHandler implements QueryHandler<TodosGetQuery, Set<Todo>> {

    @Inject
    TodoRepository todoRepository;

    @ConsumeEvent(value = "todos.get")
    @WithSession
    @WithSpan("handleTodosGetCommand")
    @Override
    public Uni<Result<Set<Todo>, Error>> execute(final TodosGetQuery query) {
        final Either<String, Set<TodoId>> usernameOrIds = query.usernameOrIds();
        final Uni<Stream<Todo>> todos = switch (usernameOrIds) {
            case Left(final String username) -> todoRepository.list(username);
            case Right(final Set<TodoId> ids) -> todoRepository.listBy(ids);
        };

        return todos.map(todoStream -> todoStream.collect(Collectors.toSet()))
                .map(Result::ok);
    }
}