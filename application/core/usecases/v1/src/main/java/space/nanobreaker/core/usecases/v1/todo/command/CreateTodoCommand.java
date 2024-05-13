package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.core.usecases.v1.Command;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateTodoCommand(
        String username,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements Serializable, Command {
}