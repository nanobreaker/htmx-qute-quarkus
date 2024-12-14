package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.command.Command;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoListTest extends ParserTestBase {

    @Test
    void parse_todo_list_help_command() {
        var input = "todo list help";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.List.Help());
    }

    @Test
    void parse_todo_list_all() {
        var input = "todo list all";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.List.All());
    }

    @Test
    void parse_todo_list_with_args() {
        var input = "todo list \"1\" \"2\"";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.List.ByIds(Set.of(1, 2)));
    }

    @Test
    void parse_todo_list_with_filter() {
        var input = "todo list -f\"title\"";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.List.ByFilters(Set.of("title")));
    }

    @Test
    void parse_todo_list_with_args_filter() {
        var input = "todo list \"1\" \"2\" -f\"title\"";
        var result = parser.parse(input);

        assertThat(result.isOk()).isTrue();
        assertThat(result.ok()).contains(new Command.Todo.List.ByIdsAndFilters(Set.of(1, 2), Set.of("title")));
    }

    @Test
    void return_error_when_arg_is_missing() {
        var input = "todo list";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.Empty());
    }
}