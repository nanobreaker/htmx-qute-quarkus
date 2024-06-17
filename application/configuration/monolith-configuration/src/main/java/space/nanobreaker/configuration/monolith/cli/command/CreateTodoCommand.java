package space.nanobreaker.configuration.monolith.cli.command;

import java.util.Objects;

public record CreateTodoCommand(
        String description,
        Range range
) implements TodoCommand {

    public CreateTodoCommand {
        Objects.requireNonNull(description);
    }

    public String help() {
        return """
                  usage: todo create [options]

                  options:
                  -d --description  string              description
                  -s --start        dd/mm/yy hh:mm:ss   start date(time),
                  -e --end          dd/mm/yy hh:mm:ss   end date(time)
                """;
    }
}

