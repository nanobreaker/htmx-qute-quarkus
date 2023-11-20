package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.core.usecases.v1.Command;

import java.util.UUID;

public record DeleteTodoCommand(UUID id) implements Command {
}