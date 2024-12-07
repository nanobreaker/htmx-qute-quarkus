package space.nanobreaker.configuration.monolith.services.tokenizer;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.library.error.Error;

import java.util.SequencedCollection;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTodoUpdateTest extends TokenizerTestBase {

    @Test
    void shouldReturnProgTodoAndCmdUpdate() {
        final String input = "todo update";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Cmd.Update()
        );
    }
}