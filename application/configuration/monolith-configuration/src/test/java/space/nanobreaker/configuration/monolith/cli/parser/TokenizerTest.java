package space.nanobreaker.configuration.monolith.cli.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.nanobreaker.configuration.monolith.cli.parser.token.*;
import space.nanobreaker.configuration.monolith.extension.Err;
import space.nanobreaker.configuration.monolith.extension.Ok;
import space.nanobreaker.configuration.monolith.extension.Result;

import java.util.SequencedCollection;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTest {

    private Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
    }

    @AfterEach
    void tearDown() {
    }

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

    @Test
    void shouldReturnProgramToken() {
        final String input = "todo";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Ok.class);

        final SequencedCollection<Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Program.Todo());
    }

    @Test
    void shouldReturnProgramCommandTokens() {
        final String input = "todo create";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Ok.class);

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

        assertThat(result).isInstanceOf(Ok.class);

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

        assertThat(result).isInstanceOf(Ok.class);

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

        assertThat(result).isInstanceOf(Ok.class);

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
    void shouldReturnErrorWhenInputIsEmptyOrNull() {
        final String input = "";
        final Result<SequencedCollection<Token>, TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Err.class);
    }
}