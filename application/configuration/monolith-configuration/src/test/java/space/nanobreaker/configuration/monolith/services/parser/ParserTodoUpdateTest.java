package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.EndDateTime;
import space.nanobreaker.configuration.monolith.services.command.StartDateTime;
import space.nanobreaker.configuration.monolith.services.command.UpdateTodoCmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoUpdateTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        final Tokenizer tokenizer = new Tokenizer();
        parser = new Parser(tokenizer);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldReturnTodoUpdateCmd() {
        final String input = new ParserTodoUpdateTest.InputBuilder("todo")
                .append("update")
                .append("\"title1\"")
                .append("\"title2\"")
                .append("-t\"new title\"")
                .append("-d\"description\"")
                .append("-s\"27/06/2024 15:00\"")
                .append("-e\"27/06/2024 16:00\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new UpdateTodoCmd(
                Set.of("title1", "title2"),
                "new title",
                "description",
                StartDateTime.of(
                        LocalDate.of(2024, 6, 27),
                        LocalTime.of(15, 0)
                ),
                EndDateTime.of(
                        LocalDate.of(2024, 6, 27),
                        LocalTime.of(16, 0)
                )
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
