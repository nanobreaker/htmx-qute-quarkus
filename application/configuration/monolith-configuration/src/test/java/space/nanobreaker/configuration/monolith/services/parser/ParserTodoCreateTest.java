package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCommand;
import space.nanobreaker.library.error.Error;
import io.github.dcadea.jresult.Result;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoCreateTest extends ParserTestBase {

    @Test
    void shouldCreateCmd() {
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27.06.24 12:30\"")
                .append("-e\"27/06/2024 13:30\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCommand(
                "test",
                "getDescription",
                LocalDateTime.of(2024, 6, 27, 12, 30),
                LocalDateTime.of(2024, 6, 27, 13, 30)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreTimes() {
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"12:30\"")
                .append("-e\"13:30\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCommand(
                "test",
                "getDescription",
                LocalDateTime.of(this.year, this.month, this.day, 12, 30),
                LocalDateTime.of(this.year, this.month, this.day, 13, 30)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreFullDates() {
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27.06.24\"")
                .append("-e\"27.06.24\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCommand(
                "test",
                "getDescription",
                LocalDateTime.of(2024, 6, 27, 0, 0),
                LocalDateTime.of(2024, 6, 27, 0, 0)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreJustDayOfTheMonth() {
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27\"")
                .append("-e\"28\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCommand(
                "test",
                "getDescription",
                LocalDateTime.of(this.year, this.month, 27, 0, 0),
                LocalDateTime.of(this.year, this.month, 28, 0, 0)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreDayOfTheMonthAndMonth() {
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27.06\"")
                .append("-e\"28.06\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCommand(
                "test",
                "getDescription",
                LocalDateTime.of(this.year, 6, 27, 0, 0),
                LocalDateTime.of(this.year, 6, 28, 0, 0)
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }
}
