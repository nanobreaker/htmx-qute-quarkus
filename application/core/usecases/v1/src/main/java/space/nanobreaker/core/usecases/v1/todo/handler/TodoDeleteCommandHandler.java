package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.command.TodoDeleteCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

@ApplicationScoped
public class TodoDeleteCommandHandler implements CommandHandler<TodoDeleteCommand> {

    @Inject
    TodoRepository todoRepository;

    @WithSpan("deleteTodoCommandHandler execute")
    @ConsumeEvent(value = "deleteTodoCommand")
    @Transactional
    public Result<Void, Error> execute(final TodoDeleteCommand todoDeleteCommand) {
//        return todoRepository.deleteByTodoId(todoDeleteCommand.id())
//                .replaceWith(todoDeleteCommand.id());
        return Result.ok(null);
    }

}
