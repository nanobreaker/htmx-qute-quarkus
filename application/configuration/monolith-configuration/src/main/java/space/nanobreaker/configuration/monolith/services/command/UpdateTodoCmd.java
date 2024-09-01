package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.Objects;
import java.util.Set;

public record UpdateTodoCmd(
        Set<String> filters,
        String title,
        String description,
        StartDateTime start,
        EndDateTime end
) implements TodoCmd {


    public UpdateTodoCmd {
        Objects.requireNonNull(filters);
        assert !filters.isEmpty();
    }

    public static Result<Command, Error> of(
            final Set<String> searchPatterns,
            final String title,
            final String description,
            final StartDateTime start,
            final EndDateTime end) {
        try {
            return Result.ok(new UpdateTodoCmd(searchPatterns, title, description, start, end));
        } catch (Exception e) {
            return Result.err(new CmdErr.CreationFailed(e.getMessage()));
        }
    }

    public static String help() {
        return """
                   \s
                   usage
                   \s
                     todo update "<titles....>" [-t"<title>"] [-d"<description>"] [-s"<start>"] [-e"<end>"]
                   \s
                   argument
                   \s
                     arg       string                       title of todo
                   \s
                   options
                   \s
                     -t        string                       title of todo
                     -d        string                       description of todo
                     -s        template dd/mm/yy hh:mm      start date time
                     -e        template dd/mm/yy hh:mm      end date time
                   \s
                   examples
                   \s
                     todo update "trip to barcelona" -d"check in day before" -s"21 09:00" -e"22 18:00"
                     todo update "doggy" -d"buy new bottle"
                   \s
                """;
    }

    public static final class UpdateTodoCmdBuilder {
        private Set<String> filters;
        private String title;
        private String description;
        private StartDateTime start;
        private EndDateTime end;

        public UpdateTodoCmdBuilder() {
        }

        public static UpdateTodoCmdBuilder anUpdateTodoCmd() {
            return new UpdateTodoCmdBuilder();
        }

        public UpdateTodoCmdBuilder withFilters(Set<String> filters) {
            this.filters = filters;
            return this;
        }

        public UpdateTodoCmdBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public UpdateTodoCmdBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UpdateTodoCmdBuilder withStart(StartDateTime start) {
            this.start = start;
            return this;
        }

        public UpdateTodoCmdBuilder withEnd(EndDateTime end) {
            this.end = end;
            return this;
        }

        public Result<Command, Error> build() {
            return UpdateTodoCmd.of(filters, title, description, start, end);
        }
    }
}