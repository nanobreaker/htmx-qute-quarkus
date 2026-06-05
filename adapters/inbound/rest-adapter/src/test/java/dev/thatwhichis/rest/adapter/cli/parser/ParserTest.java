package dev.thatwhichis.rest.adapter.cli.parser;

import dev.thatwhichis.rest.adapter.cli.tokenizer.Token;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest extends ParserTestBase {

    @Test
    void return_error_when_input_is_empty() {
        var input = "";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.Empty());
    }

    @Test
    void return_error_when_program_is_unknown() {
        var input = "test";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.UnknownToken("test"));
    }

    @Test
    void return_error_when_command_is_unknown() {
        var input = "todo test";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.UnknownCommand(new Token.Unknown("test").toString()));
    }
}