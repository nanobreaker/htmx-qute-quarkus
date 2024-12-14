package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTodoDeleteTest extends TokenizerTestBase {

    @Test
    void tokenize_todo_delete() {
        var input = "todo delete";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.DELETE)
        );
    }

    @Test
    void tokenize_todo_delete_help() {
        var input = "todo delete help";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.DELETE),
                new Token.Keyword(KEYWORD.HELP)
        );
    }

    @Test
    void tokenize_todo_delete_all() {
        var input = "todo delete all";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.DELETE),
                new Token.Keyword(KEYWORD.ALL)
        );
    }

    @Test
    void tokenize_todo_delete_arg_arg_arg() {
        var input = "todo delete \"1\" \"2\" \"3\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.DELETE),
                new Token.Text("1"),
                new Token.Text("2"),
                new Token.Text("3")
        );
    }
}