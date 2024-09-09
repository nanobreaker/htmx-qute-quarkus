package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Arg;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Cmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Prog;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Token;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.SequencedCollection;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTodoListTest extends TokenizerTestBase {

    @Test
    void shouldReturnProgTodoAndCmdList() {
        final String input = "todo list";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.List()
        );
    }

    @Test
    void shouldReturnProgTodoAndCmdListAndArgs() {
        final String input = "todo list \"id_one\" \"id_two\" \"id_three\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(5);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.List(),
                new Arg("id_one"),
                new Arg("id_two"),
                new Arg("id_three")
        );
    }
}