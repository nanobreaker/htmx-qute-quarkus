package space.nanobreaker.core.usecases.v1.todo;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.Command.Todo.Update;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoEvent;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.ddd.EventDispatcher;
import space.nanobreaker.library.error.Error;

import java.util.Set;

@ApplicationScoped
public class UpdateTodoHandler implements CommandHandler<Update, Result<Void, Error>> {

    private final EventDispatcher eventDispatcher;
    private final TodoRepository todoRepository;

    public UpdateTodoHandler(
            final EventDispatcher eventDispatcher,
            final TodoRepository todoRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.todoRepository = todoRepository;
    }

    @Override
    @ConsumeEvent(value = "command.todo.update")
    @WithSpan("handleTodoUpdateCommand")
    @WithSession
    public Uni<Result<Void, Error>> handle(final Update command) {
        return switch (command) {
            case Update.ByIds(var ids, var payload) -> {
                var resultUni = todoRepository.list(ids);

                yield resultUni.flatMap(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        yield eventDispatcher.on(
                                () -> todoRepository.update(todos, payload),
                                new TodoEvent.Updated(todos)
                        );
                    }
                    case Err(Error error) -> {
                        yield Uni.createFrom()
                                .item(Result.err(error));
                    }
                });
            }
            case Update.ByFilters(var username, var filters, var payload) -> {
                var resultUni = todoRepository.list(username, filters);

                yield resultUni.flatMap(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        yield eventDispatcher.on(
                                () -> todoRepository.update(todos, payload),
                                new TodoEvent.Updated(todos)
                        );
                    }
                    case Err(Error error) -> {
                        yield Uni.createFrom()
                                .item(Result.err(error));
                    }
                });
            }
            case Update.ByIdsAndFilters(var ids, var filters, var payload) -> {
                var resultUni = todoRepository.list(ids, filters);

                yield resultUni.flatMap(result -> switch (result) {
                    case Ok(Set<Todo> todos) -> {
                        yield eventDispatcher.on(
                                () -> todoRepository.update(todos, payload),
                                new TodoEvent.Updated(todos)
                        );
                    }
                    case Err(Error error) -> {
                        yield Uni.createFrom()
                                .item(Result.err(error));
                    }
                });
            }
        };
    }
}
