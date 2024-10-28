package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.result.Result;

import java.util.Objects;

public record CreateTodoCommand(
        String title,
        Option<String> description,
        Option<StartDateTime> start,
        Option<EndDateTime> end
) implements TodoCommand {

    public CreateTodoCommand {
        Objects.requireNonNull(title);
        assert !title.isBlank();
    }

    public CreateTodoCommand(
            final String title,
            final String description,
            final StartDateTime start,
            final EndDateTime end
    ) {
        this(
                title,
                Option.of(description),
                Option.of(start),
                Option.of(end)
        );
    }

    public static Result<Command, Error> of(
            final String title,
            final String description,
            final StartDateTime start,
            final EndDateTime end
    ) {
        return CreateTodoCommand.of(
                title,
                Option.of(description),
                Option.of(start),
                Option.of(end)
        );
    }

    public static Result<Command, Error> of(
            final String title,
            final Option<String> description,
            final Option<StartDateTime> start,
            final Option<EndDateTime> end) {
        try {
            return Result.ok(
                    new CreateTodoCommand(
                            title,
                            description,
                            start,
                            end
                    )
            );
        } catch (Exception e) {
            return Result.err(new CommandError.CreationFailed(e.getMessage()));
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