package space.nanobreaker.core.usecases.v1.todo;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.Command;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoEvent;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoIdSequenceGenerator;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.ddd.EventDispatcher;
import space.nanobreaker.library.error.Error;

@ApplicationScoped
public class CreateTodoHandler implements CommandHandler<Command.Todo.Create, Result<Todo, Error>> {

    private final EventDispatcher eventDispatcher;
    private final TodoIdSequenceGenerator idSeq;
    private final TodoRepository todoRepository;

    public CreateTodoHandler(
            final TodoRepository todoRepository,
            final TodoIdSequenceGenerator idSeq,
            final EventDispatcher eventDispatcher
    ) {
        this.todoRepository = todoRepository;
        this.idSeq = idSeq;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    @ConsumeEvent(value = "command.todo.create")
    @WithSpan("handleTodoCreateCommand")
    @WithTransaction
    public Uni<Result<Todo, Error>> handle(final Command.Todo.Create command) {
        var username = command.username();
        Uni<TodoId> id = idSeq.next(username);
        Uni<Result<Todo, Error>> createdTodo = id
                .map(todoId -> {
                    var title = command.title();
                    var builder = new Todo.Builder(todoId, title);

                    command.start().map(builder::withStart);
                    command.end().map(builder::withEnd);
                    command.description().map(builder::withDescription);

                    return builder.build();
                })
                .flatMap(todo -> eventDispatcher.on(
                        () -> todoRepository.save(todo),
                        new TodoEvent.Created(todo)
                ));

        return createdTodo.flatMap(result -> switch (result) {
            case Ok(Todo todo) -> {
                yield idSeq.increment(username).replaceWith(Result.ok(todo));
            }
            case Err(Error error) -> {
                yield Uni.createFrom().item(Result.err(error));
            }
        });
    }
}
