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

class TokenizerTodoDeleteTest extends TokenizerTestBase {

    @Test
    void shouldReturnProgTodoAndCmdDelete() {
        final String input = "todo delete";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.Delete()
        );
    }

    @Test
    void shouldReturnProgTodoAndCmdDeleteAndArgs() {
        final String input = "todo delete \"1\" \"2\" \"3\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(5);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.Delete(),
                new Arg("1"),
                new Arg("2"),
                new Arg("3")
        );
    }
}