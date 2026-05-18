package dev.thatwhichis.rest.adapter.services.parser;

import dev.thatwhichis.rest.adapter.parsing.ParserError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest extends dev.thatwhichis.rest.adapter.services.parser.ParserTestBase {

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
        assertThat(result.err()).contains(new ParserError.UnknownToken(""));
    }

    @Test
    void return_error_when_command_is_unknown() {
        var input = "todo test";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.UnknownCommand(""));
    }
}