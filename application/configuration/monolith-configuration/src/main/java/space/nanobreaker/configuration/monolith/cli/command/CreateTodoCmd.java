package space.nanobreaker.configuration.monolith.cli.command;

import space.nanobreaker.configuration.monolith.extension.Error;
import space.nanobreaker.configuration.monolith.extension.Result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.Optional;

public record CreateTodoCmd(
        String title,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements TodoCmd {

    private static final StringBuilder pattern = new StringBuilder()
            .append("[dd/MM/yyyy HH:mm]")
            .append("[dd/MM/yyyy HH]")
            .append("[dd/MM/yyyy]")
            .append("[dd/MM]")
            .append("[dd]");

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern(pattern.toString())
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.YEAR, 2024)
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 7)
            .toFormatter();

    public CreateTodoCmd {
        Objects.requireNonNull(title);
    }

    public CreateTodoCmd(
            final String title,
            final String description,
            final String startString,
            final String endString
    ) {
        this(
                title,
                description,
                Optional.of(startString)
                        .map(d -> LocalDateTime.parse(d, formatter))
                        .orElse(null),
                Optional.of(endString)
                        .map(d -> LocalDateTime.parse(d, formatter))
                        .orElse(null)
        );
    }

    public String help() {
        return """
                   usage: todo create "title" [options]

                   options:
                   -d --description  "string"           description
                   -s --start        "dd/mm/yy hh:mm"   start date(time),
                   -e --end          "dd/mm/yy hh:mm"   end date(time)
                \s
                   examples:
                   todo create "yoga" -d"eminescu street" -s"30/06/2024"
                   todo create "vacation" -s"02/07" -e"09/07"
                   todo create "walk with doggy" -s"08:00" -e"10:00"
                \s
                """;
    }

    public static final class CreateTodoCommandBuilder {
        private String title;
        private String description;
        private String start;
        private String end;

        public CreateTodoCommandBuilder() {
        }

        public static CreateTodoCommandBuilder aCreateTodoCommand() {
            return new CreateTodoCommandBuilder();
        }

        public CreateTodoCommandBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public CreateTodoCommandBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CreateTodoCommandBuilder withStart(String start) {
            this.start = start;
            return this;
        }

        public CreateTodoCommandBuilder withEnd(String end) {
            this.end = end;
            return this;
        }

        public Result<Command, Error> build() {
            try {
                return Result.ok(new CreateTodoCmd(title, description, start, end));
            } catch (Exception e) {
                return Result.err(new CmdErr.CreationFailed(e.getMessage()));
            }
        }
    }

}

