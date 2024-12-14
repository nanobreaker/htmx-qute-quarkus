package space.nanobreaker.configuration.monolith.services.parser;

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
        assertThat(result.err()).contains(new ParserError.UnknownProgram());
    }

    @Test
    void return_error_when_command_is_unknown() {
        var input = "todo test";
        var result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.UnknownCommand());
    }
}