package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.core.domain.v1.todo.TodoId;

import java.util.Set;

public record DeleteTodoCmd(
        Set<TodoId> ids
) implements TodoCmd {

    public static String help() {
        return """
                   \s
                   usage
                   \s
                     todo delete "<arg>"
                   \s
                   argument
                   \s
                     arg       string                       id of todo
                   \s
                   examples
                   \s
                     todo delete "1" "2" "3"
                   \s
                """;
    }
}
