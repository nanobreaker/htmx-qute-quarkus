package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCmd;
import space.nanobreaker.configuration.monolith.services.command.UpdateTodoCmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest {

    private Tokenizer tokenizer;
    private Parser parser;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
        parser = new Parser(tokenizer);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldReturnTodoCreateCmd() {
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"description\"")
                .append("-s\"27/06/2024\"")
                .append("-e\"27/06/2024\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCmd(
                "test",
                "description",
                LocalDateTime.of(2024, 6, 27, 0, 0),
                LocalDateTime.of(2024, 6, 27, 0, 0)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldReturnTodoUpdateCmd() {
        final String input = new InputBuilder("todo")
                .append("update")
                .append("\"title1\"")
                .append("\"title2\"")
                .append("-t\"new title\"")
                .append("-d\"description\"")
                .append("-s\"27/06/2024\"")
                .append("-e\"27/06/2024\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new UpdateTodoCmd(
                Set.of("title1", "title2"),
                "new title",
                "description",
                LocalDateTime.of(2024, 6, 27, 0, 0),
                LocalDateTime.of(2024, 6, 27, 0, 0)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    private static class InputBuilder {

        private final StringBuffer input;

        private InputBuilder(final String initial) {
            this.input = new StringBuffer(initial).append(" ");
        }

        InputBuilder append(String input) {
            this.input.append(input);
            this.input.append(" ");
            return this;
        }

        String build() {
            return input.toString().trim();
        }
    }
}