package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.cqrs.Command;

public record DeleteTodo(TodoId id) implements Command {

}