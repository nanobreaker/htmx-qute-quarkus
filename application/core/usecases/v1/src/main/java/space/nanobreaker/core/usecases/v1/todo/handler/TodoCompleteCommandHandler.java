package space.nanobreaker.core.usecases.v1.todo.handler;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.command.TodoCompleteCommand;
import space.nanobreaker.cqrs.CommandHandler;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

@ApplicationScoped
public class TodoCompleteCommandHandler implements CommandHandler<TodoCompleteCommand> {

    @Inject
    TodoRepository todoRepository;

    @WithSpan("completeTodoCommandHandler execute")
    @ConsumeEvent(value = "completeTodoCommand")
    @Transactional
    public Result<Void, Error> execute(final TodoCompleteCommand todoCompleteCommand) {
//        return todoRepository.findByTodoId(todoCompleteCommand.id())
//                .replaceWith(todoCompleteCommand.id());
        return Result.ok(null);
    }

}
