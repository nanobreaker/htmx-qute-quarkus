package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.*;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.SequencedCollection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * examples on possible programs and commands:
 * <p>
 * todo create "yoga" -d"eminescu street" -s"29/06/2024"
 * todo create "vacationing" -d"let's go to Spain!" -s"02/07/2024" -e"09/07/2024"
 * todo list "1"
 * todo update "1" -d"make vacation longer" -e"30/07/2025"
 * todo delete "1" "2" "3"
 * calendar show
 * user show
 * <p>
 * todo         create      "vacationing"       -d"let's go to Spain!"  -s"02/07/2024"  -e"09/07/2024"
 * program      command     argument(value)     option(value)           option(value)   option(value)
 * <p>
 * todo         delete      "1"                 "2"                 "3"
 * program      command     argument(value)     argument(value)     argument(value)
 * <p>
 * calendar     show
 * program      command
 * <p>
 * user         show
 * program      command
 */
class TokenizerTest {

    private Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
    }

    @AfterEach
    void tearDown() {
    }

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

    @Test
    void shouldReturnProgramCommandArgumentsTokens() {
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