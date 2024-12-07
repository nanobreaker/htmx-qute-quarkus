package space.nanobreaker.configuration.monolith.services.tokenizer;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.library.error.Error;

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
                new Token.Prog.Todo(),
                new Token.Cmd.List()
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
                new Token.Prog.Todo(),
                new Token.Cmd.List(),
                new Token.Arg("id_one"),
                new Token.Arg("id_two"),
                new Token.Arg("id_three")
        );
    }
}