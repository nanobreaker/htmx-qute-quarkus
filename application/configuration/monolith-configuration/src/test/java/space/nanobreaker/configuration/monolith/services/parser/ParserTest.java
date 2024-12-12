package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.library.error.Error;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest extends ParserTestBase {

    @Test
    void return_error_when_input_is_empty() {
        var input = "";
        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.Empty());
    }

    @Test
    void return_error_when_program_is_unknown() {
        var input = new InputBuilder("test").build();
        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.UnknownProgram());
    }

    @Test
    void return_error_when_command_is_unknown() {
        var input = new InputBuilder("todo").append("test").build();
        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.err()).contains(new ParserError.UnknownCommand());
    }
}