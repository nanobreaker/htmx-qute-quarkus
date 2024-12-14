package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTest extends TokenizerTestBase {

    @Test
    void no_tokens_when_input_is_empty() {
        var input = "";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).isEmpty();
    }

    @Test
    void tokenize_known_keyword() {
        var input = "help";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).contains(new Token.Keyword(KEYWORD.HELP));
    }

    @Test
    void tokenize_unknown_keyword() {
        var input = "donthelp";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).contains(new Token.Keyword(KEYWORD.UNKNOWN));
    }

    @Test
    void tokenize_known_and_unknown_keywords() {
        var input = "todo crea";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.UNKNOWN)
        );
    }

    @Test
    void tokenize_unknown_tokens() {
        var input = "tod crea";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.UNKNOWN),
                new Token.Keyword(KEYWORD.UNKNOWN)
        );
    }

    @Test
    void tokenize_incomplete_text_token_as_unknown() {
        var input = "todo create \"yog";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Unknown("yog")
        );
    }

    @Test
    void tokenize_unknown_option() {
        var input = "todo create \"yoga\" -x\"wtf\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Text("yoga"),
                new Token.Option(OPTION.UNKNOWN),
                new Token.Text("wtf")
        );
    }

    @Test
    void tokenize_empty_text() {
        var input = "\"\" \" \"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Text(""),
                new Token.Text(" ")
        );
    }

    @Test
    void tokenize_empty_option_as_unknown() {
        var input = "todo create -\"22\"";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.TODO),
                new Token.Keyword(KEYWORD.CREATE),
                new Token.Option(OPTION.UNKNOWN),
                new Token.Text("22")
        );
    }
}