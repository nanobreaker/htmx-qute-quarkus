package space.nanobreaker.configuration.monolith.cli.command;

import space.nanobreaker.core.domain.v1.TodoId;

import java.util.Set;

public record DeleteTodoCmd(
        Set<TodoId> ids
) implements TodoCmd {

    public String help() {
        return """
                  usage:
                   todo delete <id(s)>

                  example:
                  todo delete 11 21
                """;
    }
}
