package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.cqrs.Command;

import java.util.Set;

public record DeleteTodos(Set<TodoId> ids) implements Command {

}