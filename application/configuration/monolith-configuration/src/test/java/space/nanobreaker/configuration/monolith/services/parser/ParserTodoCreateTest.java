package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoCreateTest {

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
                .append("create")
                .append("\"test\"")
                .append("-d\"description\"")
                .append("-s\"27.06.24 12:30\"")
                .append("-e\"27/06/2024 13:30\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCmd(
                "test",
                "description",
                LocalDateTime.of(2024, 6, 27, 12, 30),
                LocalDateTime.of(2024, 6, 27, 13, 30)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateTodoCreateCmdWhenStartAndEndAreTime() {
        final LocalDateTime now = LocalDateTime.now();
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"description\"")
                .append("-s\"" + now.getDayOfMonth() + " 12:30\"")
                .append("-e\"" + now.getDayOfMonth() + " 13:30\"")
                .build();


        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCmd(
                "test",
                "description",
                LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 12, 30),
                LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 13, 30)
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
