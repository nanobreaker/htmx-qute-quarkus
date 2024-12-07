package space.nanobreaker.configuration.monolith.services.tokenizer;

import io.github.dcadea.jresult.Result;
import org.junit.jupiter.api.Test;
import space.nanobreaker.library.error.Error;

import java.util.SequencedCollection;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTest extends TokenizerTestBase {

    @Test
    void shouldReturnProgramToken() {
        String input = "todo";
        Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Token.Prog.Todo());
    }

    @Test
    void shouldReturnUnknownTokenWhenProgramIsNotComplete() {
        final String input = "tod";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Token.Unk("tod"));
    }

    @Test
    void shouldReturnUnknownTokenWhenCommandIsNotComplete() {
        final String input = "todo crea";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Unk("crea")
        );
    }

    @Test
    void shouldReturnUnknownTokensWhenProgramAndCommandAreNotComplete() {
        final String input = "tod crea";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Token.Unk("tod"),
                new Token.Unk("crea")
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenArgumentIsNotComplete() {
        final String input = "todo create \"yog";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(3);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Cmd.Create(),
                new Token.Unk("yog")
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenOptionIsNotComplete() {
        final String input = "todo create \"yoga\" -d\"not compl";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(4);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Cmd.Create(),
                new Token.Arg("yoga"),
                new Token.Unk("not compl")
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenOptionIsNotSpecified() {
        final String input = "todo create -d";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Cmd.Create()
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenOptionIsNotKnown() {
        final String input = "todo create \"yoga\" -x\"wtf\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(4);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Cmd.Create(),
                new Token.Arg("yoga"),
                new Token.Unk("wtf")
        );
    }

    @Test
    void shouldReturnUnknowns() {
        final String input = "some crazy shit bla bla -j\"\" 243989fdfljsdlfkfdlk \" \"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(8);
        assertThat(tokens).containsExactly(
                new Token.Unk("some"),
                new Token.Unk("crazy"),
                new Token.Unk("shit"),
                new Token.Unk("bla"),
                new Token.Unk("bla"),
                new Token.Unk(""),
                new Token.Unk("243989fdfljsdlfkfdlk"),
                new Token.Arg(" ")
        );
    }

    @Test
    void shouldReturnUnknownsAndIgnoreSpecialSymbols() {
        final String input = "hey yo, what the fuck, are you failing?!";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(8);
        assertThat(tokens).containsExactly(
                new Token.Unk("hey"),
                new Token.Unk("yo"),
                new Token.Unk("what"),
                new Token.Unk("the"),
                new Token.Unk("fuck"),
                new Token.Unk("are"),
                new Token.Unk("you"),
                new Token.Unk("failing")
        );
    }

    @Test
    void shouldNotFailOnOptionWithoutKey() {
        final String input = "todo create \"test\" -\"22\" -\"24\"";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(5);
        assertThat(tokens).containsExactly(
                new Token.Prog.Todo(),
                new Token.Cmd.Create(),
                new Token.Arg("test"),
                new Token.Unk("22"),
                new Token.Unk("24")
        );
    }

    @Test
    void shouldReturnErrorWhenInputIsEmptyOrNull() {
        final String input = "";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isErr()).isTrue();
    }
}