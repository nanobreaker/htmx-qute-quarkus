package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.command.Command;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static space.nanobreaker.library.option.Option.some;

public class ParserTodoCreateTest extends ParserTestBase {

    @Test
    void parse_todo_create_help_command() {
        var input = "todo create help";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.Create.Help());
    }

    @Test
    void parse_todo_create_command_with_title_description_start_end() {
        var input = "todo create \"title\" -d\"description\" -s\"27.06.24 12:30\" -e\"13:30\"";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok())
                .contains(new Command.Todo.Create.Default(
                        "title",
                        some("description"),
                        some(LocalDateTime.of(2024, 6, 27, 12, 30)),
                        some(LocalDateTime.of(this.year, this.month, this.day, 13, 30))
                ));
    }

    @Test
    void return_error_when_arg_is_missing() {
        var input = "todo create -d\"test\"";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.ArgumentNotFound());
    }

    @Test
    void return_error_when_more_than_one_arg() {
        var input = "todo create \"title1\" \"title2\"";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.RedundantArgs());
    }

    @Test
    void return_error_when_date_is_invalid() {
        var input = "todo create \"title\" -s\"not a date\"";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err())
                .contains(new ParserError.DateParseError("Text 'not a date' could not be parsed, unparsed text found at index 0"));
    }
}
