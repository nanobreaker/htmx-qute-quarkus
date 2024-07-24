package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.cqrs.Command;

import java.io.Serializable;
import java.time.LocalDateTime;

public record TodoCreateCommand(
        String username,
        String title,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements Serializable, Command {
}