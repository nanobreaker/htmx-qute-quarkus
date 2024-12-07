package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.library.error.Error;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static space.nanobreaker.library.option.Option.some;

public class ParserTodoCreateTest extends ParserTestBase {

    @Test
    void shouldCreateCmd() {
        String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27.06.24 12:30\"")
                .append("-e\"27/06/2024 13:30\"")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        Command expectedCommand = new Command.Todo.Create.Default(
                "test",
                some("getDescription"),
                some(LocalDateTime.of(2024, 6, 27, 12, 30)),
                some(LocalDateTime.of(2024, 6, 27, 13, 30))
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreTimes() {
        String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"12:30\"")
                .append("-e\"13:30\"")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        Command expectedCommand = new Command.Todo.Create.Default(
                "test",
                some("getDescription"),
                some(LocalDateTime.of(this.year, this.month, this.day, 12, 30)),
                some(LocalDateTime.of(this.year, this.month, this.day, 13, 30))
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreFullDates() {
        String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27.06.24\"")
                .append("-e\"27.06.24\"")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        Command expectedCommand = new Command.Todo.Create.Default(
                "test",
                some("getDescription"),
                some(LocalDateTime.of(2024, 6, 27, 0, 0)),
                some(LocalDateTime.of(2024, 6, 27, 0, 0))
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreJustDayOfTheMonth() {
        String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27\"")
                .append("-e\"28\"")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        Command expectedCommand = new Command.Todo.Create.Default(
                "test",
                some("getDescription"),
                some(LocalDateTime.of(this.year, this.month, 27, 0, 0)),
                some(LocalDateTime.of(this.year, this.month, 28, 0, 0))
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreDayOfTheMonthAndMonth() {
        String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"getDescription\"")
                .append("-s\"27.06\"")
                .append("-e\"28.06\"")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        Command expectedCommand = new Command.Todo.Create.Default(
                "test",
                some("getDescription"),
                some(LocalDateTime.of(this.year, 6, 27, 0, 0)),
                some(LocalDateTime.of(this.year, 6, 28, 0, 0))
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }
}
