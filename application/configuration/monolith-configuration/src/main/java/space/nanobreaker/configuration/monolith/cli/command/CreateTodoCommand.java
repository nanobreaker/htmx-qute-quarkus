package space.nanobreaker.configuration.monolith.cli.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;

public record CreateTodoCommand(
        String title,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements TodoCommand {

    public CreateTodoCommand {
        Objects.requireNonNull(title);
    }

    public CreateTodoCommand(
            String title,
            String description,
            String startString,
            String endString
    ) {
        final StringBuilder pattern = new StringBuilder()
                .append("[dd/MM/yyyy HH:mm]")
                .append("[dd/MM/yyyy HH]")
                .append("[dd/MM/yyyy]")
                .append("[dd/MM]")
                .append("[dd]");

        final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern(pattern.toString())
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.YEAR, 2024)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 7)
                .toFormatter();

        final LocalDateTime startDate = LocalDateTime.parse(startString, formatter);
        final LocalDateTime endDate = LocalDateTime.parse(endString, formatter);

        this(title, description, startDate, endDate);
    }

    public String help() {
        return """
                  usage: todo create "title" [options]

                  options:
                  -d --description  "string"           description
                  -s --start        "dd/mm/yy hh:mm"   start date(time),
                  -e --end          "dd/mm/yy hh:mm"   end date(time)
                 
                  examples:
                  todo create "yoga" -d"eminescu street" -s"30/06/2024"
                  todo create "vacation" -s"02/07" -e"09/07"
                  todo create "walk with doggy" -s"08:00" -e"10:00"
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

        public CreateTodoCommand build() {
            return new CreateTodoCommand(title, description, start, end);
        }
    }
}

