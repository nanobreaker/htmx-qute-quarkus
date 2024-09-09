package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.tokenizer.TokenizerErr;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest extends ParserTestBase {

    @Test
    void shouldReturnErrorOnBlankInput() {
        final String input = "";
        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.error()).isInstanceOf(TokenizerErr.EmptyInput.class);
    }

    @Test
    void shouldReturnErrorOnUnknownProgram() {
        final String input = new InputBuilder("test").build();
        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.error()).isInstanceOf(ParserErr.UnknownProgram.class);
    }

    @Test
    void shouldReturnErrorOnUnknownCommand() {
        final String input = new InputBuilder("todo")
                .append("test")
                .build();
        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isErr()).isTrue();
        assertThat(result.error()).isInstanceOf(ParserErr.UnknownCommand.class);
    }
}