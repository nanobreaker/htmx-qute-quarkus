package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.DeleteTodoCmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoDeleteTest {

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
    void shouldCreateTodoCreateCmdWhenStartAndEndAreDateTime() {
        final String input = new InputBuilder("todo")
                .append("delete")
                .append("\"1\"")
                .append("\"2\"")
                .append("\"3\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new DeleteTodoCmd(Set.of(1, 2, 3));

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
