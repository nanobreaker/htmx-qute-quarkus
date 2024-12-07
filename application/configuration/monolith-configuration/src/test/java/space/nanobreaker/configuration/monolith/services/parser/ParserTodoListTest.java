package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.library.error.Error;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoListTest extends ParserTestBase {

    @Test
    void shouldCreateListCmd() {
        String input = new InputBuilder("todo")
                .append("list")
                .append("all")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        Command expectedCommand = new Command.Todo.List.All();

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateListCmdWithArgs() {
        String input = new InputBuilder("todo")
                .append("list")
                .append("\"1\"")
                .append("\"2\"")
                .append("\"3\"")
                .build();

        Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        Command actualCommand = result.unwrap();
        Command expectedCommand = new Command.Todo.List.ByIds(Set.of(1, 2, 3));

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }
}