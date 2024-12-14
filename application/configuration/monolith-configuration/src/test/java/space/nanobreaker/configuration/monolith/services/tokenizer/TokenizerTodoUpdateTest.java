package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTodoUpdateTest extends TokenizerTestBase {

    @Test
    void tokenize_todo_update() {
        var input = "todo update";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.UPDATE)
        );
    }

    @Test
    void tokenize_todo_update_help() {
        var input = "todo update help";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.UPDATE),
                new Token.Keyword(KEYWORD.HELP)
        );
    }

    @Test
    void tokenize_todo_update_arg_title() {
        var input = "todo update \"id\" -t\"new title\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.UPDATE),
                new Token.Text("id"),
                new Token.Option(OPTION.TITLE),
                new Token.Text("new title")
        );
    }

    @Test
    void tokenize_todo_update_arg_arg_title_description_start_end() {
        var input = "todo update \"id_one\" \"id_two\" -t\"new title\" -d\"new desc\" -s\"12:00\" -e\"16:00\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.UPDATE),
                new Token.Text("id_one"),
                new Token.Text("id_two"),
                new Token.Option(OPTION.TITLE),
                new Token.Text("new title"),
                new Token.Option(OPTION.DESCRIPTION),
                new Token.Text("new desc"),
                new Token.Option(OPTION.START),
                new Token.Text("12:00"),
                new Token.Option(OPTION.END),
                new Token.Text("16:00")
        );
    }

    @Test
    void tokenize_todo_update_fitler_title() {
        var input = "todo update -f\"title_filter\" -t\"new title\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.UPDATE),
                new Token.Option(OPTION.FILTER),
                new Token.Text("title_filter"),
                new Token.Option(OPTION.TITLE),
                new Token.Text("new title")
        );
    }

    @Test
    void tokenize_todo_update_filter_title_description_start_end() {
        var input = "todo update -f\"title_filter\" -t\"new title\" -d\"new desc\" -s\"12:00\" -e\"16:00\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.UPDATE),
                new Token.Option(OPTION.FILTER),
                new Token.Text("title_filter"),
                new Token.Option(OPTION.TITLE),
                new Token.Text("new title"),
                new Token.Option(OPTION.DESCRIPTION),
                new Token.Text("new desc"),
                new Token.Option(OPTION.START),
                new Token.Text("12:00"),
                new Token.Option(OPTION.END),
                new Token.Text("16:00")
        );
    }
}