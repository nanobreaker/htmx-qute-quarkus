package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.common.InputBuilder;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.DeleteTodoCommand;
import space.nanobreaker.library.error.Error;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTodoDeleteTest extends ParserTestBase {

    @Test
    void shouldNotCreateDeleteCmd() {
        final String input = new InputBuilder("todo")
                .append("delete")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isFalse();

        final Error error = result.unwrapErr();

        assertThat(error).isInstanceOf(ParserError.ArgumentNotFound.class);
    }

    @Test
    void shouldCreateDeleteCmdWithArgs() {
        final String input = new InputBuilder("todo")
                .append("delete")
                .append("\"1\"")
                .append("\"2\"")
                .append("\"3\"")
                .build();

        final Result<Command, Error> result = parser.parse(input);

        assertThat(result.isOk()).isTrue();

        final Command actualCommand = result.unwrap();
        final Command expectedCommand = new DeleteTodoCommand(Set.of(1, 2, 3));

        assertThat(actualCommand).isEqualTo(expectedCommand);
    }
}