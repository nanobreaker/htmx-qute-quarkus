package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.UpdateTodoCommand;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.result.Result;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoUpdateTest extends ParserTestBase {

    @Test
    void shouldReturnTodoUpdateCmd() {
        final String input = new InputBuilder("todo")
                .append("update")
                .append("\"1\"")
                .append("-f\"title1,title2\"")
                .append("-t\"new title\"")
                .append("-d\"getDescription\"")
                .append("-s\"27/06/2024 15:00\"")
                .append("-e\"27/06/2024 16:00\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new UpdateTodoCommand(
                Set.of(1),
                List.of("title1", "title2"),
                "new title",
                "getDescription",
                ZonedDateTime.of(2024, 6, 27, 15, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2024, 6, 27, 16, 0, 0, 0, ZoneId.of("UTC"))
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }
}