package space.nanobreaker.configuration.monolith.cli.parser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.cli.command.Command;
import space.nanobreaker.configuration.monolith.cli.command.CreateTodoCmd;
import space.nanobreaker.configuration.monolith.cli.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.cli.tokenizer.token.*;
import space.nanobreaker.configuration.monolith.extension.Error;
import space.nanobreaker.configuration.monolith.extension.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.Optional;
import java.util.SequencedCollection;

@ApplicationScoped
public class Parser {

    @Inject
    Tokenizer tokenizer;

    private static final StringBuilder pattern = new StringBuilder()
            .append("[dd/MM/yyyy HH:mm]")
            .append("[dd/MM/yyyy HH]")
            .append("[dd/MM/yyyy]")
            .append("[dd/MM]")
            .append("[dd]");

    // todo: remove hardcoded values and use current date & time
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern(pattern.toString())
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.YEAR, 2024)
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 7)
            .toFormatter();

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
        final Result<Arg, Error> argumentResult = getArgument(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(new ParserErr.ArgumentNotFound());

        final Arg arg = argumentResult.unwrap();
        final Option<Opt.Description> descriptionOption = Option.over(getOption(tokens, Opt.Description.class));
        final Option<Opt.Start> startOption = Option.over(getOption(tokens, Opt.Start.class));
        final Option<Opt.End> endOption = Option.over(getOption(tokens, Opt.End.class));
        record TodoCreateBox(Arg arg, Option<Opt.Description> desc, Option<Opt.Start> start, Option<Opt.End> end) {
        }
        final TodoCreateBox box = new TodoCreateBox(arg, descriptionOption, startOption, endOption);

        return switch (box) {
            case TodoCreateBox(
                    Arg(final String title),
                    None<Opt.Description> _,
                    None<Opt.Start> _,
                    None<Opt.End> _
            ) -> CreateTodoCmd.of(title, null, null, null);
            case TodoCreateBox(
                    Arg(final String title),
                    Some<Opt.Description>(Opt.Description(final String description)),
                    None<Opt.Start> _,
                    None<Opt.End> _
            ) -> CreateTodoCmd.of(title, description, null, null);
            case TodoCreateBox(
                    Arg(final String title),
                    Some<Opt.Description>(Opt.Description(final String description)),
                    Some<Opt.Start>(Opt.Start(final String startStr)),
                    None<Opt.End> _
            ) -> {
                final Result<LocalDateTime, Error> start = parseDate(startStr);
                if (start.isErr())
                    yield Result.err(start.error());

                yield CreateTodoCmd.of(title, description, start.unwrap(), null);
            }
            case TodoCreateBox(
                    Arg(final String title),
                    Some<Opt.Description>(Opt.Description(final String description)),
                    Some<Opt.Start>(Opt.Start(final String startStr)),
                    Some<Opt.End>(Opt.End(final String endStr))
            ) -> {
                final Result<LocalDateTime, Error> start = parseDate(startStr);
                if (start.isErr())
                    yield Result.err(start.error());

                final Result<LocalDateTime, Error> end = parseDate(endStr);
                if (end.isErr())
                    yield Result.err(end.error());

                yield CreateTodoCmd.of(title, description, start.unwrap(), end.unwrap());
            }
            // todo: add handling for case where just end date is specified?
            default -> Result.err(new ParserErr.NotSupportedOperation());
        };
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

    private static Result<LocalDateTime, Error> parseDate(final String string) {
        try {
            return Result.ok(LocalDateTime.parse(string, formatter));
        } catch (DateTimeParseException e) {
            return Result.err(new ParserErr.DateTimeParseErr(e.getParsedString()));
        }
    }
}