package space.nanobreaker.configuration.monolith.services.parser;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.*;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.*;

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

    @WithSpan("parseInputString")
    public Result<Command, Error> parse(final String input) {
        // input = todo create "todo" -d"fjsdlkfjsdlf" -s"12/12/2024" -e"12/12/2024"

        final Result<SequencedCollection<Token>, Error> tokenizerResult = tokenizer.tokenize(input);

        if (tokenizerResult.isErr())
            return Result.err(tokenizerResult.error());

        final SequencedCollection<Token> tokens = tokenizerResult.unwrap();
        final Token programToken = tokens.removeFirst();

        return switch (programToken) {
            case Prog.Todo ignored -> parseTodoProgram(tokens);
            case Prog.Calendar ignored -> parseCalendarProgram(tokens);
            case Prog.User ignored -> parseUserProgram(tokens);
            default -> Result.err(new ParserErr.UnknownProgram());
        };
    }

    private Result<Command, Error> parseTodoProgram(final SequencedCollection<Token> tokens) {
        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Create ignored -> parseTodoCreateCommand(tokens);
            case Cmd.List ignored -> parseTodoListCommand(tokens);
            case Cmd.Update ignored -> parseTodoUpdateCommand(tokens);
            case Cmd.Delete ignored -> parseTodoDeleteCommand(tokens);
            default -> Result.err(new ParserErr.UnknownCommand());
        };
    }

    private Result<Command, Error> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        // final Arg argumentResult = getArgument(tokens, Arg.class)?;

        final Result<Arg, Error> argumentResult = getArgument(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(new ParserErr.ArgumentNotFound());

        final Arg arg = argumentResult.unwrap();
        final Option<Opt.Description> descriptionOption = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startOption = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endOption = getOption(tokens, Opt.End.class);

        record TodoCreateBox(
                Arg arg,
                Option<Opt.Description> desc,
                Option<Opt.Start> start,
                Option<Opt.End> end) {
        }
        final TodoCreateBox box = new TodoCreateBox(arg, descriptionOption, startOption, endOption);

        return switch (box) {
            case TodoCreateBox(
                    Arg(final String title),
                    None<Opt.Description> ignored1,
                    None<Opt.Start> ignored2,
                    None<Opt.End> ignored3
            ) -> CreateTodoCmd.of(title, null, null, null);
            case TodoCreateBox(
                    Arg(final String title),
                    Some(Opt.Description(final String description)),
                    None<Opt.Start> ignored1,
                    None<Opt.End> ignored2
            ) -> CreateTodoCmd.of(title, description, null, null);
            case TodoCreateBox(
                    Arg(final String title),
                    Some(Opt.Description(final String description)),
                    Some(Opt.Start(final String startStr)),
                    None<Opt.End> ignored
            ) -> {
                final Result<LocalDateTime, Error> start = parseDate(startStr);
                if (start.isErr())
                    yield Result.err(start.error());

                yield CreateTodoCmd.of(title, description, start.unwrap(), null);
            }
            case TodoCreateBox(
                    Arg(final String title),
                    Some(Opt.Description(final String description)),
                    Some(Opt.Start(final String startStr)),
                    Some(Opt.End(final String endStr))
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

    private static <T> Option<T> getOption(
            final Collection<? extends Token> opts,
            final Class<T> target
    ) {
        final Optional<T> optional = opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast);

        return Option.over(optional);
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