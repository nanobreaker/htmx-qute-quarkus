package dev.thatwhichis.rest.adapter.services.parser;

import dev.thatwhichis.rest.adapter.services.command.Command;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static dev.thatwhichis.library.option.Option.none;
import static dev.thatwhichis.library.option.Option.some;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoCommandUpdateTest extends ParserTestBase {

    @Test
    void parse_todo_update_help_command() {
        var input = "todo update help";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.Update.Help());
    }

    @Test
    void parse_todo_update_command_with_args_title_description_start_end() {
        var input = "todo update \"1\" \"2\" -d\"text\" -s\"27.06.25 12:30\" -e\"13:30\"";
        var result = parser.parse(input);

        assertThat(result.ok())
                .contains(new Command.Todo.Update.ByIds(
                        Set.of(1, 2),
                        new Command.Todo.Update.Payload(
                                none(),
                                some("text"),
                                some(LocalDateTime.of(2025, 6, 27, 12, 30)),
                                some(LocalDateTime.of(this.year, this.month, this.day, 13, 30))
                        )
                ));
    }

    @Test
    void parse_todo_update_command_with_filter_title_description_start_end() {
        var input = "todo update -f\"title\" -d\"text\" -s\"27.06.25 12:30\" -e\"13:30\"";
        var result = parser.parse(input);

        assertThat(result.ok())
                .contains(new Command.Todo.Update.ByFilters(
                        Set.of("title"),
                        new Command.Todo.Update.Payload(
                                none(),
                                some("text"),
                                some(LocalDateTime.of(2025, 6, 27, 12, 30)),
                                some(LocalDateTime.of(this.year, this.month, this.day, 13, 30))
                        )
                ));
    }

    @Test
    void parse_todo_update_command_with_args_filter_title_description_start_end() {
        var input = "todo update \"1\" \"2\" -f\"title\" -d\"text\" -s\"27.06.25 12:30\" -e\"13:30\"";
        var result = parser.parse(input);

        assertThat(result.ok())
                .contains(new Command.Todo.Update.ByIdsAndFilters(
                        Set.of(1, 2),
                        Set.of("title"),
                        new Command.Todo.Update.Payload(
                                none(),
                                some("text"),
                                some(LocalDateTime.of(2025, 6, 27, 12, 30)),
                                some(LocalDateTime.of(this.year, this.month, this.day, 13, 30))
                        )
                ));
    }

    @Test
    void return_error_when_arg_is_missing() {
        var input = "todo update -d\"test\"";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.ArgumentOrFilterNotFound());
    }

    @Test
    void return_error_when_date_is_invalid() {
        var input = "todo update \"title\" -s\"not a date\"";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err())
                .contains(new ParserError.DateParseError("Text 'not a date' could not be parsed, unparsed text found at index 0"));
    }
}