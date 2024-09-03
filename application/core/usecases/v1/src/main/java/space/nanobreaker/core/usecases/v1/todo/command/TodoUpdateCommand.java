package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.cqrs.Command;
import space.nanobreaker.library.Option;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record TodoUpdateCommand(
        String username,
        Option<Set<TodoId>> ids,
        Option<List<String>> filters,
        Option<String> title,
        Option<String> description,
        Option<LocalDateTime> start,
        Option<LocalDateTime> end
) implements Serializable, Command {
}