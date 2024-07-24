package space.nanobreaker.configuration.monolith.cli.command;

import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.time.LocalDateTime;
import java.util.Objects;

public record CreateTodoCmd(
        String title,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements TodoCmd {

    public CreateTodoCmd {
        Objects.requireNonNull(title);
    }

    public static Result<Command, Error> of(
            final String title,
            final String description,
            final LocalDateTime start,
            final LocalDateTime end) {
        try {
            return Result.ok(new CreateTodoCmd(title, description, start, end));
        } catch (Exception e) {
            return Result.err(new CmdErr.CreationFailed(e.getMessage()));
        }
    }

    public static String help() {
        return """
                  usage: todo create "arg" [options]
                  \s
                  argument:
                  "arg"             "string"           title
                  \s
                  options:
                  -d --description  "string"           description
                  -s --start        "dd/mm/yy hh:mm"   start date(time),
                  -e --end          "dd/mm/yy hh:mm"   end date(time)
                  \s
                  examples:
                  todo create "yoga" -d"eminescu street" -s"30/06/2024"
                  todo create "vacation" -s"02/07" -e"09/07"
                  todo create "walk with doggy" -s"08:00" -e"10:00"
                """;
    }

}