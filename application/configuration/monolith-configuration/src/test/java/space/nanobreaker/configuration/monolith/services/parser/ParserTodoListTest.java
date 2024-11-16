package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.ListTodoCommand;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import io.github.dcadea.jresult.Result;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoListTest extends ParserTestBase {

    @Test
    void shouldCreateListCmd() {
        final String input = new InputBuilder("todo")
                .append("list")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new ListTodoCommand(Option.none());

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    @Test
    void shouldCreateListCmdWithArgs() {
        final String input = new InputBuilder("todo")
                .append("list")
                .append("\"1\"")
                .append("\"2\"")
                .append("\"3\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new ListTodoCommand(Option.of(Set.of(1, 2, 3)));

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }
}