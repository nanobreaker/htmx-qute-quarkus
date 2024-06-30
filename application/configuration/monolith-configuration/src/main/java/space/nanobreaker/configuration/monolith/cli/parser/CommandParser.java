package space.nanobreaker.configuration.monolith.cli.parser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.cli.command.CliCommand;
import space.nanobreaker.configuration.monolith.cli.command.CreateTodoCommand;
import space.nanobreaker.configuration.monolith.cli.parser.token.*;
import space.nanobreaker.configuration.monolith.extension.Err;
import space.nanobreaker.configuration.monolith.extension.Result;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.SequencedCollection;

@ApplicationScoped
public class CommandParser {

    @Inject
    Tokenizer tokenizer;

    public Result<CliCommand, Exception> parse(final String input) {
        final Result<SequencedCollection<Token>, TokenizerError> tokenizerResult = tokenizer.tokenize(input);

        if (tokenizerResult.isErr())
            return new Err<>(new IllegalStateException("tokenizer error"));

        final SequencedCollection<Token> tokens = tokenizerResult.unwrap();
        final Token programToken = tokens.removeFirst();

        return switch (programToken) {
            case Program.Todo _ -> parseTodoProgram(tokens);
            case Program.Calendar _ -> parseCalendarProgram(tokens);
            case Program.User _ -> parseUserProgram(tokens);
            default -> Result.err(new IllegalStateException("unknown command"));
        };
    }

    private Result<CliCommand, Exception> parseTodoProgram(final SequencedCollection<Token> tokens) {
        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Command.Create _ -> parseTodoCreateCommand(tokens);
            case Command.List _ -> parseTodoListCommand(tokens);
            case Command.Update _ -> parseTodoUpdateCommand(tokens);
            case Command.Delete _ -> parseTodoDeleteCommand(tokens);
            default -> Result.err(new IllegalStateException("unknown todo command"));
        };
    }

    private Result<CliCommand, Exception> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        // title        - argument
        // description  - option
        // start        - option
        // end          - option
        final CreateTodoCommand.CreateTodoCommandBuilder builder = new CreateTodoCommand.CreateTodoCommandBuilder();
        final Result<Argument, Exception> argumentResult = getArgument(tokens, Argument.class);

        if (argumentResult.isErr())
            return new Err<>(new IllegalStateException("todo argument not found"));

        argumentResult
                .map(argument -> builder.withTitle(argument.value()));

        getOption(tokens, Option.Description.class)
                .map(description -> builder.withDescription(description.value()));

        getOption(tokens, Option.Start.class)
                .map(start -> builder.withStart(LocalDate.parse(start.value())));

        getOption(tokens, Option.End.class)
                .map(end -> builder.withEnd(LocalDate.parse(end.value())));

        return Result.ok(builder.build());
    }

    private Result<CliCommand, Exception> parseTodoListCommand(final SequencedCollection<Token> tokens) {
        return Result.err(new IllegalStateException("not implemented"));
    }

    private Result<CliCommand, Exception> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        return Result.err(new IllegalStateException("not implemented"));
    }

    private Result<CliCommand, Exception> parseTodoDeleteCommand(final SequencedCollection<Token> tokens) {
        return Result.err(new IllegalStateException("not implemented"));
    }

    private Result<CliCommand, Exception> parseCalendarProgram(final SequencedCollection<Token> tokens) {
        return Result.err(new IllegalStateException("not implemented"));
    }

    private Result<CliCommand, Exception> parseUserProgram(final SequencedCollection<Token> tokens) {
        return Result.err(new IllegalStateException("not implemented"));
    }

    private static <T> Optional<T> getOption(
            final Collection<? extends Token> opts,
            final Class<T> target
    ) {
        return opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast);
    }

    private static <T> Result<T, Exception> getArgument(
            final Collection<? extends Token> opts,
            final Class<T> target
    ) {
        return opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast)
                .map(Result::<T, Exception>ok)
                .orElse(Result.err(new IllegalStateException()));
    }
}