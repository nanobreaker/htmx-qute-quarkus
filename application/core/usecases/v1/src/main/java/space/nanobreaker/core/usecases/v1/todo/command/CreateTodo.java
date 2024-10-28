package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.cqrs.Command;
import space.nanobreaker.library.option.Option;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CreateTodo(
        String username,
        String title,
        Option<String> description,
        Option<LocalDateTime> start,
        Option<LocalDateTime> end
) implements Serializable, Command {

}