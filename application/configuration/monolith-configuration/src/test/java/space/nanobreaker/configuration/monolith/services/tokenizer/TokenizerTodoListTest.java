package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTodoListTest extends TokenizerTestBase {

    @Test
    void tokenize_todo_list() {
        var input = "todo list";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.LIST)
        );
    }

    @Test
    void tokenize_todo_list_help() {
        var input = "todo list help";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.LIST),
                new Token.Keyword(KEYWORD.HELP)
        );
    }

    @Test
    void tokenize_todo_list_all() {
        var input = "todo list all";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.LIST),
                new Token.Keyword(KEYWORD.ALL)
        );
    }

    @Test
    void tokenize_todo_list_arg_arg_arg() {
        var input = "todo list \"id_one\" \"id_two\" \"id_three\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.LIST),
                new Token.Text("id_one"),
                new Token.Text("id_two"),
                new Token.Text("id_three")
        );
    }

    @Test
    void tokenize_todo_list_filter() {
        var input = "todo list -f\"title_filter\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.LIST),
                new Token.Option(OPTION.FILTER),
                new Token.Text("title_filter")
        );
    }

    @Test
    void tokenize_todo_list_arg_arg_filter() {
        var input = "todo list \"id_one\" \"id_two\" -f\"title_filter\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.LIST),
                new Token.Text("id_one"),
                new Token.Text("id_two"),
                new Token.Option(OPTION.FILTER),
                new Token.Text("title_filter")
        );
    }
}