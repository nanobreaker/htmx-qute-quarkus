package space.nanobreaker.configuration.monolith.services.tokenizer;

import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.*;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.result.Result;

import java.util.SequencedCollection;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTest extends TokenizerTestBase {

    @Test
    void shouldReturnProgramToken() {
        final String input = "todo";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Prog.Todo());
    }

    @Test
    void shouldReturnUnknownTokenWhenProgramIsNotComplete() {
        final String input = "tod";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Unk("tod"));
    }

    @Test
    void shouldReturnUnknownTokenWhenCommandIsNotComplete() {
        final String input = "todo crea";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Prog.Todo(),
                new Unk("crea")
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
                new Unk("tod"),
                new Unk("crea")
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
                new Prog.Todo(),
                new Cmd.Create(),
                new Unk("yog")
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
                new Prog.Todo(),
                new Cmd.Create(),
                new Arg("yoga"),
                new Unk("not compl")
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
                new Prog.Todo(),
                new Cmd.Create()
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
                new Prog.Todo(),
                new Cmd.Create(),
                new Arg("yoga"),
                new Unk("wtf")
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
                new Unk("some"),
                new Unk("crazy"),
                new Unk("shit"),
                new Unk("bla"),
                new Unk("bla"),
                new Unk(""),
                new Unk("243989fdfljsdlfkfdlk"),
                new Arg(" ")
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
                new Unk("hey"),
                new Unk("yo"),
                new Unk("what"),
                new Unk("the"),
                new Unk("fuck"),
                new Unk("are"),
                new Unk("you"),
                new Unk("failing")
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
                new Prog.Todo(),
                new Cmd.Create(),
                new Arg("test"),
                new Unk("22"),
                new Unk("24")
        );
    }

    @Test
    void shouldReturnErrorWhenInputIsEmptyOrNull() {
        final String input = "";
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        assertThat(result.isErr()).isTrue();
    }
}