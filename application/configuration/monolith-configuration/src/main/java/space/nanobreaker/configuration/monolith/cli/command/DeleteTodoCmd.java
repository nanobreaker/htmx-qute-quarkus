package space.nanobreaker.configuration.monolith.cli.command;

import space.nanobreaker.core.domain.v1.todo.TodoId;

import java.util.Set;

public record DeleteTodoCmd(
        Set<TodoId> ids
) implements TodoCmd {

    public static String help() {
        return """
                  usage: todo delete <id(s)>
                  \s
                  argument:
                  "id(s)"       "string"        id or list of ids, where id is todo title
                  \s
                  example:
                  todo delete 11 21
                """;
    }
}
