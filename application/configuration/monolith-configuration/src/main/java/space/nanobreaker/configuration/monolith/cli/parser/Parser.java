package space.nanobreaker.configuration.monolith.cli.parser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.cli.command.Command;
import space.nanobreaker.configuration.monolith.cli.command.CreateTodoCmd;
import space.nanobreaker.configuration.monolith.cli.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.cli.tokenizer.token.*;
import space.nanobreaker.configuration.monolith.extension.Error;
import space.nanobreaker.configuration.monolith.extension.Result;

import java.util.Collection;
import java.util.Optional;
import java.util.SequencedCollection;

@ApplicationScoped
public class Parser {

    @Inject
    Tokenizer tokenizer;

    public Result<Command, Error> parse(final String input) {
        final Result<SequencedCollection<Token>, Error> tokenizerResult = tokenizer.tokenize(input);

        if (tokenizerResult.isErr())
            return Result.err(tokenizerResult.error());

        final SequencedCollection<Token> tokens = tokenizerResult.unwrap();
        final Token programToken = tokens.removeFirst();

        return switch (programToken) {
            case Prog.Todo _ -> parseTodoProgram(tokens);
            case Prog.Calendar _ -> parseCalendarProgram(tokens);
            case Prog.User _ -> parseUserProgram(tokens);
            default -> Result.err(new ParserErr.UnknownProgram());
        };
    }

    private Result<Command, Error> parseTodoProgram(final SequencedCollection<Token> tokens) {
        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Create _ -> parseTodoCreateCommand(tokens);
            case Cmd.List _ -> parseTodoListCommand(tokens);
            case Cmd.Update _ -> parseTodoUpdateCommand(tokens);
            case Cmd.Delete _ -> parseTodoDeleteCommand(tokens);
            default -> Result.err(new ParserErr.UnknownCommand());
        };
    }

    private Result<Command, Error> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        final CreateTodoCmd.CreateTodoCommandBuilder builder = CreateTodoCmd.CreateTodoCommandBuilder.aCreateTodoCommand();
        final Result<Arg, Error> argumentResult = getArgument(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(new ParserErr.ArgumentNotFound());

        argumentResult
                .map(arg -> builder.withTitle(arg.value()));

        getOption(tokens, Opt.Description.class)
                .map(description -> builder.withDescription(description.value()));

        getOption(tokens, Opt.Start.class)
                .map(start -> builder.withStart(start.value()));

        getOption(tokens, Opt.End.class)
                .map(end -> builder.withEnd(end.value()));

        return builder.build();
    }

    private Result<Command, Error> parseTodoListCommand(final SequencedCollection<Token> tokens) {
        return Result.err(new ParserErr.NotSupportedOperation());
    }

    private Result<Command, Error> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        return Result.err(new ParserErr.NotSupportedOperation());
    }

    private Result<Command, Error> parseTodoDeleteCommand(final SequencedCollection<Token> tokens) {
        return Result.err(new ParserErr.NotSupportedOperation());
    }

    private Result<Command, Error> parseCalendarProgram(final SequencedCollection<Token> tokens) {
        return Result.err(new ParserErr.NotSupportedOperation());
    }

    private Result<Command, Error> parseUserProgram(final SequencedCollection<Token> tokens) {
        return Result.err(new ParserErr.NotSupportedOperation());
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

    private static <T> Result<T, Error> getArgument(
            final Collection<? extends Token> opts,
            final Class<T> target
    ) {
        return opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast)
                .map(Result::<T, Error>ok)
                .orElse(Result.err(new ParserErr.ArgumentNotFound()));
    }
}