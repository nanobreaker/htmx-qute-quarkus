package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.cqrs.Command;
import space.nanobreaker.library.option.Option;

import java.io.Serializable;
import java.time.ZonedDateTime;

public record CreateTodo(
        String username,
        String title,
        Option<String> description,
        Option<ZonedDateTime> start,
        Option<ZonedDateTime> end
) implements Serializable, Command {

}