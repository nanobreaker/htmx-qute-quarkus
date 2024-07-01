package space.nanobreaker.configuration.monolith.cli.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.cli.parser.token.*;
import space.nanobreaker.configuration.monolith.extension.Result;

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
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Program.Todo());
    }

    @Test
    void shouldReturnProgramCommandTokens() {
        final String input = "todo create";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.Create()
        );
    }

    @Test
    void shouldReturnProgramAndCommandAndTitleTokens() {
        final String input = "todo create \"yoga\"";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(3);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.Create(),
                new Argument("yoga")
        );
    }

    @Test
    void shouldReturnProgramCommandTitleDescriptionTokens() {
        final String input = "todo create \"yoga\" -d\"Igor the best yogin in Moldova gives yoga lesson\"";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(4);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.Create(),
                new Argument("yoga"),
                new Option.Description("Igor the best yogin in Moldova gives yoga lesson")
        );
    }

    @Test
    void shouldReturnProgramCommandTitleDescriptionStartTokens() {
        final String input = "todo create \"yoga\" -d\"Igor the best yogin in Moldova gives yoga lesson\" -s\"27/06/2024\"";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(5);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.Create(),
                new Argument("yoga"),
                new Option.Description("Igor the best yogin in Moldova gives yoga lesson"),
                new Option.Start("27/06/2024")
        );
    }

    @Test
    void shouldReturnProgramCommandArgumentsTokens() {
        final String input = "todo list \"id_one\" \"id_two\" \"id_three\"";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(5);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.List(),
                new Argument("id_one"),
                new Argument("id_two"),
                new Argument("id_three")
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenProgramIsNotComplete() {
        final String input = "tod";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Unknown("tod"));
    }

    @Test
    void shouldReturnUnknownTokenWhenCommandIsNotComplete() {
        final String input = "todo crea";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Unknown("crea")
        );
    }

    @Test
    void shouldReturnUnknownTokensWhenProgramAndCommandAreNotComplete() {
        final String input = "tod crea";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Unknown("tod"),
                new Unknown("crea")
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenArgumentIsNotComplete() {
        final String input = "todo create \"yog";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(3);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.Create(),
                new Unknown("yog")
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenOptionIsNotComplete() {
        final String input = "todo create \"yoga\" -d\"not compl";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(4);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.Create(),
                new Argument("yoga"),
                new Unknown("not compl")
        );
    }

    @Test
    void shouldReturnUnknownTokenWhenOptionIsNotKnown() {
        final String input = "todo create \"yoga\" -x\"wtf\"";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(4);
        assertThat(tokens).containsExactly(
                new Program.Todo(),
                new Command.Create(),
                new Argument("yoga"),
                new Unknown("wtf")
        );
    }

    @Test
    void shouldReturnUnknowns() {
        final String input = "some crazy shit bla bla -j\"\" 243989fdfljsdlfkfdlk \" \"";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isOk()).isTrue();

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(8);
        assertThat(tokens).containsExactly(
                new Unknown("some"),
                new Unknown("crazy"),
                new Unknown("shit"),
                new Unknown("bla"),
                new Unknown("bla"),
                new Unknown(""),
                new Unknown("243989fdfljsdlfkfdlk"),
                new Argument(" ")
        );
    }


    @Test
    void shouldReturnErrorWhenInputIsEmptyOrNull() {
        final String input = "";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result.isErr()).isTrue();
    }
}