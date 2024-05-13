package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.core.domain.v1.TodoId;
import space.nanobreaker.core.usecases.v1.Command;

import java.util.UUID;

public record CompleteTodoCommand(TodoId id) implements Command {
}