package space.nanobreaker.configuration.monolith.services.command;

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
        assert !title.isBlank();
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
                   \s
                   usage
                   \s
                     todo create "<arg>" [-d"<description>"] [-s"<start>"] [-e"<end>"]
                   \s
                   argument
                   \s
                     arg       string                       title of todo
                   \s
                   options
                   \s
                     -d        string                       description of todo
                     -s        template dd/mm/yy hh:mm      start date time
                     -e        template dd/mm/yy hh:mm      end date time
                   \s
                   examples
                   \s
                     todo create "country trip" -d"prepare car" -s"21 09:00" -e"22 18:00"
                     todo create "vacation" -d"barcelona again?" -s"04/12" -e"22/12"
                   \s
                """;
    }

}