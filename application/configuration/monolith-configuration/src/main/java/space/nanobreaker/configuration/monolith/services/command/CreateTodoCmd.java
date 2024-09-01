package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.time.LocalDateTime;
import java.util.Objects;

public record CreateTodoCmd(
        String title,
        String description,
        StartDateTime start,
        EndDateTime end
) implements TodoCmd {

    public CreateTodoCmd {
        Objects.requireNonNull(title);
        assert !title.isBlank();
    }

    public static Result<Command, Error> of(
            final String title,
            final String description,
            final StartDateTime start,
            final EndDateTime end) {
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

    public static final class CreateTodoCmdBuilder {
        private String title;
        private String description;
        private StartDateTime start;
        private EndDateTime end;

        public CreateTodoCmdBuilder() {
        }

        public static CreateTodoCmdBuilder aCreateTodoCmd() {
            return new CreateTodoCmdBuilder();
        }

        public CreateTodoCmdBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public CreateTodoCmdBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CreateTodoCmdBuilder withStart(StartDateTime start) {
            this.start = start;
            return this;
        }

        public CreateTodoCmdBuilder withEnd(EndDateTime end) {
            this.end = end;
            return this;
        }

        public Result<Command, Error> build() {
            return CreateTodoCmd.of(title, description, start, end);
        }
    }
}