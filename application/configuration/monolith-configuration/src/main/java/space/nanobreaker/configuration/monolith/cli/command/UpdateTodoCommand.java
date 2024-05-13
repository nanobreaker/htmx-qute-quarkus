package space.nanobreaker.configuration.monolith.cli.command;

import space.nanobreaker.core.domain.v1.TodoId;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateTodoCommand(
        Set<TodoId> ids,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements TodoCommand {

    public String help() {
        return """
                  usage:
                   todo update <id(s)> [options]

                  options:
                  -d --description string     description
                  -s --start string           start date time
                  -e --end string             end date time
                  -p --priority string        priority

                  example:
                  todo update 11 21 -s 11/12/2024
                """;
    }
}
