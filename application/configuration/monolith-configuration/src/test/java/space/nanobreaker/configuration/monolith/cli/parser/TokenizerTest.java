package space.nanobreaker.configuration.monolith.cli.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    void shouldReturnSingleProgramToken() {
        final String input = "todo";
        final Result<SequencedCollection<Tokenizer.Token>, CommandParser.TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Ok.class);

        final SequencedCollection<Tokenizer.Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens).contains(new Tokenizer.Program.TodoProgram());
    }

    @Test
    void shouldReturnBothProgramAndCommandTokens() {
        final String input = "todo create";
        final Result<SequencedCollection<Tokenizer.Token>, CommandParser.TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Ok.class);

        final SequencedCollection<Tokenizer.Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(2);
        assertThat(tokens).containsExactly(
                new Tokenizer.Program.TodoProgram(),
                new Tokenizer.Todo.Create()
        );
    }

    @Test
    void shouldReturnProgramAndCommandAndTitleTokens() {
        final String input = "todo create -t\"yoga\"";
        final Result<SequencedCollection<Tokenizer.Token>, CommandParser.TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Ok.class);

        final SequencedCollection<Tokenizer.Token> tokens = result.unwrap();

        assertThat(tokens.size()).isEqualTo(3);
        assertThat(tokens).containsExactly(
                new Tokenizer.Program.TodoProgram(),
                new Tokenizer.Todo.Create(),
                new Tokenizer.TodoOption.Title("yoga")
        );
    }

    @Test
    void shouldReturnTokens() {
        final String input = "todo create -t\"yoga\" -d\"Igor' the best yogin in Moldova gives yoga lesson\" -s\"26/06/2024\"";
        final Result<SequencedCollection<Tokenizer.Token>, CommandParser.TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Ok.class);
    }

    @Test
    void shouldReturnError() {
        final String input = "";
        final Result<SequencedCollection<Tokenizer.Token>, CommandParser.TokenizerError> result = tokenizer.tokenize(input);

        assertThat(result).isInstanceOf(Err.class);
    }
}