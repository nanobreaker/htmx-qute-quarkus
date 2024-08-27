package space.nanobreaker.core.usecases.v1.todo.command;

import space.nanobreaker.cqrs.Command;
import space.nanobreaker.cqrs.Query;

import java.io.Serializable;
import java.util.Set;

public record TodoListCommand(
        String username,
        Set<Integer> ids
) implements Serializable, Command {
}