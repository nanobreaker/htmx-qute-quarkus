package space.nanobreaker.configuration.monolith.services.tokenizer;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.library.error.Error;

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
                new Token.Prog.Todo(),
                new Token.Cmd.Delete()
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
                new Token.Prog.Todo(),
                new Token.Cmd.Delete(),
                new Token.Arg("1"),
                new Token.Arg("2"),
                new Token.Arg("3")
        );
    }

    @Test
    void shouldReturnProgTodoAndCmdDeleteAndSubCmdAll() {
        final String input = "todo delete all";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(3);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Cmd.Delete(),
                new Token.SubCmd.All()
        );
    }
}