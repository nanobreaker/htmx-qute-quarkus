package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTodoCreateTest extends TokenizerTestBase {

    @Test
    void tokenize_todo_create() {
        var input = "todo create";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE)
        );
    }

    @Test
    void tokenize_todo_create_help() {
        var input = "todo create help";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Keyword(KEYWORD.HELP)
        );
    }

    @Test
    void tokenize_todo_create_arg() {
        var input = "todo create \"yoga\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Text("yoga")
        );
    }

    @Test
    void tokenize_todo_create_arg_description() {
        var input = "todo create \"yoga\" -d\"Igor the best yogin in Moldova gives yoga lesson\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Text("yoga"),
                new Token.Option(OPTION.DESCRIPTION),
                new Token.Text("Igor the best yogin in Moldova gives yoga lesson")
        );
    }

    @Test
    void tokenize_todo_create_arg_start() {
        var input = "todo create \"yoga\" -s\"27/06/2024\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Text("yoga"),
                new Token.Option(OPTION.START),
                new Token.Text("27/06/2024")
        );
    }

    @Test
    void tokenize_todo_create_arg_end() {
        var input = "todo create \"yoga\" -e\"13:00\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Text("yoga"),
                new Token.Option(OPTION.END),
                new Token.Text("13:00")
        );
    }

    @Test
    void tokenize_todo_create_arg_description_start_end() {
        var input = "todo create \"yoga\" -d\"celentanos\" -s\"27/06/2024 15:00\" -e\"13:00\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Text("yoga"),
                new Token.Option(OPTION.DESCRIPTION),
                new Token.Text("celentanos"),
                new Token.Option(OPTION.START),
                new Token.Text("27/06/2024 15:00"),
                new Token.Option(OPTION.END),
                new Token.Text("13:00")
        );
    }
}