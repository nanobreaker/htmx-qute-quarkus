package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.result.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record UpdateTodoCommand(
        Option<Set<Integer>> ids,
        Option<List<String>> filters,
        Option<String> title,
        Option<String> description,
        Option<LocalDateTime> start,
        Option<LocalDateTime> end
) implements TodoCommand {

    public UpdateTodoCommand {
        if (ids.isNone() && filters.isNone())
            throw new IllegalStateException("idsOrUsername and filters cannot be empty");
    }

    public UpdateTodoCommand(
            Set<Integer> ids,
            List<String> filters,
            String title,
            String description,
            LocalDateTime start,
            LocalDateTime end
    ) {
        this(
                Option.of(ids),
                Option.of(filters),
                Option.of(title),
                Option.of(description),
                Option.of(start),
                Option.of(end)
        );
    }

    public static Result<Command, Error> of(
            Option<Set<Integer>> ids,
            Option<List<String>> filters,
            Option<String> title,
            Option<String> description,
            Option<LocalDateTime> start,
            Option<LocalDateTime> end
    ) {
        try {
            return Result.ok(
                    new UpdateTodoCommand(
                            ids,
                            filters,
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

    public static Result<Command, Error> of(
            final Set<Integer> ids,
            final List<String> titleFilters,
            final String title,
            final String description,
            final LocalDateTime start,
            final LocalDateTime end
    ) {
        try {
            return Result.ok(
                    new UpdateTodoCommand(
                            ids,
                            titleFilters,
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
                     todo update "<arg(s)>" [-t"<title>"] [-d"<description>"] [-s"<start>"] [-e"<end>"]
                   \s
                   argument
                   \s
                     arg       string                       id of todo
                   \s
                   options
                   \s
                     -f        string                       filter by todo title
                     -t        string                       title of todo
                     -d        string                       description of todo
                     -s        template dd/mm/yy hh:mm      start date time
                     -e        template dd/mm/yy hh:mm      end date time
                   \s
                   examples
                   \s
                     todo update -f"trip to barcelona" -d"check in day before" -s"21 09:00" -e"22 18:00"
                     todo update -f"doggy" -d"buy new bottle"
                     todo update "0" -d"buy new bottle"
                   \s
                """;
    }
}