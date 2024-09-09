package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Cmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Prog;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Token;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

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
                new Prog.Todo(),
                new Cmd.Update()
        );
    }

}