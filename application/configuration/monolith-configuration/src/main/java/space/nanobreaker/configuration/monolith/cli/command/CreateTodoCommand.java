package space.nanobreaker.configuration.monolith.cli.command;

import java.time.LocalDate;
import java.util.Objects;

public record CreateTodoCommand(
        String title,
        String description,
        LocalDate start,
        LocalDate end
) implements TodoCommand {

    public CreateTodoCommand {
        Objects.requireNonNull(title);
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
        private LocalDate start;
        private LocalDate end;

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

        public CreateTodoCommandBuilder withStart(LocalDate start) {
            this.start = start;
            return this;
        }

        public CreateTodoCommandBuilder withEnd(LocalDate end) {
            this.end = end;
            return this;
        }

        public CreateTodoCommand build() {
            return new CreateTodoCommand(title, description, start, end);
        }
    }
}

