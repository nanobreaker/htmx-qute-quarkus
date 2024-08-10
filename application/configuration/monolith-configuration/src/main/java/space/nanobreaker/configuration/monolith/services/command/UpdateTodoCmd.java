package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public record UpdateTodoCmd(
        Set<String> filters,
        String title,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements TodoCmd {


    public UpdateTodoCmd {
        Objects.requireNonNull(filters);
        assert !filters.isEmpty();
    }

    public static Result<Command, Error> of(
            final Set<String> searchPatterns,
            final String title,
            final String description,
            final LocalDateTime start,
            final LocalDateTime end) {
        try {
            return Result.ok(new UpdateTodoCmd(searchPatterns, title, description, start, end));
        } catch (Exception e) {
            return Result.err(new CmdErr.CreationFailed(e.getMessage()));
        }
    }

    public static String help() {
        return """
                  usage: todo update "id" [options]
                  \s
                  argument:
                  "id"              "string"           id (title)
                  \s
                  options:
                  -d --description  "string"           description
                  -s --start        "dd/mm/yy hh:mm"   start date(time),
                  -e --end          "dd/mm/yy hh:mm"   end date(time)
                  \s
                  examples:
                  todo update "yoga" -d"new description"
                  todo update "vacation" -s"23/11" -e"26/11"
                  todo update "walk with doggy" -s"12:00" -e"15:00"
                """;
    }
}