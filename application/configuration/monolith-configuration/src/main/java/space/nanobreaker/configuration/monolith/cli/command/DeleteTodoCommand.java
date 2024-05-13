package space.nanobreaker.configuration.monolith.cli.command;

import space.nanobreaker.core.domain.v1.TodoId;

import java.util.Set;

public record DeleteTodoCommand(
        Set<TodoId> ids
) implements TodoCommand {

    public String help() {
        return """
                  usage:
                   todo delete <id(s)>

                  example:
                  todo delete 11 21
                """;
    }
}
