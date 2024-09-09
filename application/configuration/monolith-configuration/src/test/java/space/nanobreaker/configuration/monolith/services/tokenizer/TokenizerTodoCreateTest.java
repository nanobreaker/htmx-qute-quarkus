package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.*;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.SequencedCollection;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTodoCreateTest extends TokenizerTestBase {

    @Test
    void shouldReturnProgramCommandTokens() {
        final String input = "todo create";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.Create()
        );
    }

    @Test
    void shouldReturnProgramAndCommandAndTitleTokens() {
        final String input = "todo create \"yoga\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(3);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.Create(),
                new Arg("yoga")
        );
    }

    @Test
    void shouldReturnProgramCommandTitleDescriptionTokens() {
        final String input = "todo create \"yoga\" -d\"Igor the best yogin in Moldova gives yoga lesson\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(4);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.Create(),
                new Arg("yoga"),
                new Opt.Description("Igor the best yogin in Moldova gives yoga lesson")
        );
    }

    @Test
    void shouldReturnProgramCommandTitleDescriptionStartTokens() {
        final String input = "todo create \"yoga\" -d\"Igor the best yogin in Moldova gives yoga lesson\" -s\"27/06/2024\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(5);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.Create(),
                new Arg("yoga"),
                new Opt.Description("Igor the best yogin in Moldova gives yoga lesson"),
                new Opt.Start("27/06/2024")
        );
    }

    @Test
    void shouldReturnStartAndEndTokensWhenDatesAreDateTime() {
        final String input = "todo create \"t\" -s\"12:00\" -e\"13:00\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(5);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Cmd.Create(),
                new Arg("t"),
                new Opt.Start("12:00"),
                new Opt.End("13:00")
        );
    }
}