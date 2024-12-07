package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.library.error.Error;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static space.nanobreaker.library.option.Option.some;

public class ParserTodoUpdateTest extends ParserTestBase {

    @Test
    void shouldReturnTodoUpdateCmd() {
        String input = new InputBuilder("todo")
                .append("update")
                .append("\"1\"")
                .append("-f\"title1,title2\"")
                .append("-t\"new title\"")
                .append("-d\"getDescription\"")
                .append("-s\"27/06/2024 15:00\"")
                .append("-e\"27/06/2024 16:00\"")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        var payload = new Command.Todo.Update.Payload(
                some("new title"),
                some("getDescription"),
                some(LocalDateTime.of(2024, 6, 27, 15, 0)),
                some(LocalDateTime.of(2024, 6, 27, 16, 0))
        );
        Command expectedCommand = new Command.Todo.Update.ByIdsAndFilters(
                Set.of(1),
                Set.of("title1", "title2"),
                payload
        );

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }
}