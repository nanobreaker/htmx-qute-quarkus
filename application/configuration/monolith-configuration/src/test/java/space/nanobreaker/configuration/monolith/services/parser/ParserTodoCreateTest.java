package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCmd;
import space.nanobreaker.configuration.monolith.services.command.EndDateTime;
import space.nanobreaker.configuration.monolith.services.command.StartDateTime;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoCreateTest extends ParserTestBase {

    @Test
    void shouldCreateCmd() {
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
                StartDateTime.of(
                        LocalDate.of(2024, 6, 27),
                        LocalTime.of(12, 30)
                ),
                EndDateTime.of(
                        LocalDate.of(2024, 6, 27),
                        LocalTime.of(13, 30)
                )
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreTimes() {
        final LocalDateTime now = LocalDateTime.now();
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"description\"")
                .append("-s\"12:30\"")
                .append("-e\"13:30\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCmd(
                "test",
                "description",
                StartDateTime.of(
                        LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth()),
                        LocalTime.of(12, 30)
                ),
                EndDateTime.of(
                        LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth()),
                        LocalTime.of(13, 30)
                )
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreFullDates() {
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"description\"")
                .append("-s\"27.06.24\"")
                .append("-e\"27.06.24\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCmd(
                "test",
                "description",
                StartDateTime.of(
                        LocalDate.of(2024, 6, 27),
                        LocalTime.of(0, 0)
                ),
                EndDateTime.of(
                        LocalDate.of(2024, 6, 27),
                        LocalTime.of(0, 0)
                )
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreJustDayOfTheMonth() {
        final LocalDateTime now = LocalDateTime.now();
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"description\"")
                .append("-s\"27\"")
                .append("-e\"28\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCmd(
                "test",
                "description",
                StartDateTime.of(
                        LocalDate.of(now.getYear(), now.getMonth(), 27),
                        LocalTime.of(0, 0)
                ),
                EndDateTime.of(
                        LocalDate.of(now.getYear(), now.getMonth(), 28),
                        LocalTime.of(0, 0)
                )
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateCmdWhenStartAndEndOptionsAreDayOfTheMonthAndMonth() {
        final LocalDateTime now = LocalDateTime.now();
        final String input = new InputBuilder("todo")
                .append("create")
                .append("\"test\"")
                .append("-d\"description\"")
                .append("-s\"27.06\"")
                .append("-e\"28.06\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new CreateTodoCmd(
                "test",
                "description",
                StartDateTime.of(
                        LocalDate.of(now.getYear(), 6, 27),
                        LocalTime.of(0, 0)
                ),
                EndDateTime.of(
                        LocalDate.of(now.getYear(), 6, 28),
                        LocalTime.of(0, 0)
                )
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

}
