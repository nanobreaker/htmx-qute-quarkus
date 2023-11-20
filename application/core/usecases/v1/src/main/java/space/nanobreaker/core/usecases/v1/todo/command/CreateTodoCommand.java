package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.core.usecases.v1.Command;

import java.time.LocalDate;

public record CreateTodoCommand(String title, String description, LocalDate target) implements Command {
}