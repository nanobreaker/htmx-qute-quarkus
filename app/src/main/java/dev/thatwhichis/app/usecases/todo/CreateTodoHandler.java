package dev.thatwhichis.app.usecases.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoEvent;
import dev.thatwhichis.core.ports.inbound.todo.TodoCommand;
import dev.thatwhichis.core.ports.outbound.todo.TodoIdSequenceGenerator;
import dev.thatwhichis.core.ports.outbound.todo.TodoRepository;
import dev.thatwhichis.framework.cqrs.CommandHandler;
import dev.thatwhichis.framework.ddd.EventDispatcher;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateTodoHandler implements CommandHandler<TodoCommand.Create, Todo> {

    private final EventDispatcher eventDispatcher;
    private final TodoIdSequenceGenerator idSeq;
    private final TodoRepository todoRepository;

    @Inject
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
    @WithTransaction
    @WithSpan("handleTodoCreateCommand")
    public Uni<Result<Todo, Error>> handle(final TodoCommand.Create command) {
        var username = command.username();
        var idUni = idSeq.next(username);
        var createdTodoResUni = idUni
                .map(todoId -> {
                    var title = command.title();
                    var builder = new Todo.Builder(todoId, title);

                    command.start().map(builder::withStart);
                    command.end().map(builder::withEnd);
                    command.description().map(builder::withDescription);

                    return builder.build();
                })
                .flatMap(todo -> eventDispatcher.on(() -> todoRepository.save(todo), new TodoEvent.Created(todo)));

        return createdTodoResUni.flatMap(result -> switch (result) {
            case Ok(Todo todo) -> idSeq.increment(username).replaceWith(Result.ok(todo));
            case Err(Error error) -> Uni.createFrom().item(Result.err(error));
        });
    }
}
