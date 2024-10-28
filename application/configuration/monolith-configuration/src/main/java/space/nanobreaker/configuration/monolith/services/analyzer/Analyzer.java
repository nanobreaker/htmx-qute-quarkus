package space.nanobreaker.configuration.monolith.services.analyzer;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.CalendarCommand;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.DeleteTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.ListTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.ShowCalendarCommand;
import space.nanobreaker.configuration.monolith.services.command.ShowUserCommand;
import space.nanobreaker.configuration.monolith.services.command.TodoCommand;
import space.nanobreaker.configuration.monolith.services.command.UpdateTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.UserCommand;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.services.tokenizer.TokenizerError;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Cmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Prog;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Token;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.result.Result;

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

        if (tokens.isEmpty())
            return Result.err(new TokenizerError.EmptyInput());

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
            return Result.ok(TodoCommand.help());

        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Create ignored -> Result.ok(CreateTodoCommand.help());
            case Cmd.List ignored -> Result.ok(ListTodoCommand.help());
            case Cmd.Update ignored -> Result.ok(UpdateTodoCommand.help());
            case Cmd.Delete ignored -> Result.ok(DeleteTodoCommand.help());
            default -> Result.ok(TodoCommand.help());
        };
    }

    private Result<String, Error> analyzeCalendarProgram(final SequencedCollection<Token> tokens) {
        if (tokens.isEmpty())
            return Result.ok(CalendarCommand.help());

        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Show ignored -> Result.ok(ShowCalendarCommand.help());
            default -> Result.ok(CalendarCommand.help());
        };
    }

    private Result<String, Error> analyzeUserProgram(final SequencedCollection<Token> tokens) {
        if (tokens.isEmpty())
            return Result.ok(UserCommand.help());

        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Show ignored -> Result.ok(ShowUserCommand.help());
            default -> Result.ok(UserCommand.help());
        };
    }
}