package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerCalendarShowTest extends TokenizerTestBase {

    @Test
    void tokenize_calendar() {
        var input = "calendar";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(new Token.Keyword(KEYWORD.CALENDAR));
    }

    @Test
    void tokenize_calendar_help() {
        var input = "calendar help";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.CALENDAR),
                new Token.Keyword(KEYWORD.HELP)
        );
    }

    @Test
    void tokenize_calendar_show() {
        var input = "calendar show";
        var tokens = tokenizer.tokenize(input);

        assertThat(tokens).containsExactly(
                new Token.Keyword(KEYWORD.CALENDAR),
                new Token.Keyword(KEYWORD.SHOW)
        );
    }
}