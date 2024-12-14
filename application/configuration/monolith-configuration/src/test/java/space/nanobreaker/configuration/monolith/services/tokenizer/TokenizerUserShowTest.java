package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerUserShowTest extends TokenizerTestBase {

    @Test
    void tokenize_user() {
        var input = "user";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(new Token.Keyword(KEYWORD.USER));
    }

    @Test
    void tokenize_user_help() {
        var input = "user help";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.USER),
                new Token.Keyword(KEYWORD.HELP)
        );
    }

    @Test
    void tokenize_user_show() {
        var input = "user show";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.USER),
                new Token.Keyword(KEYWORD.SHOW)
        );
    }
}