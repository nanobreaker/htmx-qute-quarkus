package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.cqrs.Command;
import space.nanobreaker.library.Option;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public record TodoUpdateCommand(
        Set<String> filters,
        String username,
        Option<String> title,
        Option<String> description,
        Option<LocalDateTime> start,
        Option<LocalDateTime> end
) implements Serializable, Command {
}