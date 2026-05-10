package dev.thatwhichis.rest.adapter.services.parser;

import dev.thatwhichis.rest.adapter.services.command.Command;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoCommandDeleteTest extends ParserTestBase {

    @Test
    void parse_todo_delete_help_command() {
        var input = "todo delete help";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.Delete.Help());
    }

    @Test
    void parse_todo_delete_all() {
        var input = "todo delete all";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.Delete.All());
    }

    @Test
    void parse_todo_delete_with_args() {
        var input = "todo delete \"1\" \"2\"";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.Delete.ByIds(Set.of(1, 2)));
    }

    @Test
    void return_error_when_arg_is_missing() {
        var input = "todo delete";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.Empty());
    }
}