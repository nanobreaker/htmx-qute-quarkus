package space.nanobreaker.configuration.monolith.services.analyzer;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.*;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Cmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Prog;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Token;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.SequencedCollection;

@ApplicationScoped
public class Analyzer {

    @Inject
    Tokenizer tokenizer;

    @WithSpan("analyzeInputString")
    public Result<String, Error> analyze(final String input) {
        final Result<SequencedCollection<Token>, Error> tokenizerResult = tokenizer.tokenize(input);

        if (tokenizerResult.isErr())
            return Result.err(tokenizerResult.error());

        final SequencedCollection<Token> tokens = tokenizerResult.unwrap();
        final Token programToken = tokens.removeFirst();

        return switch (programToken) {
            case Prog.Todo ignored -> analyzeTodoProgram(tokens);
            case Prog.Calendar ignored -> analyzeCalendarProgram(tokens);
            case Prog.User ignored -> analyzeUserProgram(tokens);
            default -> Result.ok(Command.help());
        };
    }

    private Result<String, Error> analyzeTodoProgram(final SequencedCollection<Token> tokens) {
        if (tokens.isEmpty())
            return Result.ok(TodoCmd.help());

        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Create ignored -> Result.ok(CreateTodoCmd.help());
            case Cmd.List ignored -> Result.ok(ListTodoCmd.help());
            case Cmd.Update ignored -> Result.ok(UpdateTodoCmd.help());
            case Cmd.Delete ignored -> Result.ok(DeleteTodoCmd.help());
            default -> Result.ok(TodoCmd.help());
        };
    }

    private Result<String, Error> analyzeCalendarProgram(final SequencedCollection<Token> tokens) {
        if (tokens.isEmpty())
            return Result.ok(CalendarCmd.help());

        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Show ignored -> Result.ok(CalendarShowCmd.help());
            default -> Result.ok(CalendarCmd.help());
        };
    }

    private Result<String, Error> analyzeUserProgram(final SequencedCollection<Token> tokens) {
        if (tokens.isEmpty())
            return Result.ok(UserCmd.help());

        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Show ignored -> Result.ok(UserShowCmd.help());
            default -> Result.ok(UserCmd.help());
        };
    }
}