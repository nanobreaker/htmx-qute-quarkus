package space.nanobreaker.configuration.monolith.services.parser;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.*;
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
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class Parser {

    private final Tokenizer tokenizer;

    @Inject
    public Parser(final Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private static final StringBuilder pattern = new StringBuilder()
            .append("[dd[/][.][-]MM[/][.][-]yyyy HH:mm]")
            .append("[dd[/][.][-]MM[/][.][-]yyyy HH]")
            .append("[dd[/][.][-]MM[/][.][-]yyyy]")
            .append("[dd[/][.][-]MM[/][.][-]yy HH:mm]")
            .append("[dd[/][.][-]MM[/][.][-]yy HH]")
            .append("[dd[/][.][-]MM[/][.][-]yy]")
            .append("[dd[/][.][-]MM HH:mm]")
            .append("[dd[/][.][-]MM HH]")
            .append("[dd[/][.][-]MM]")
            .append("[dd HH:mm]")
            .append("[dd HH]")
            .append("[dd]")
            .append("[HH:mm]");

    // todo: remove hardcoded values and use current date (year,month,day)
    private static final LocalDateTime now = LocalDateTime.now();
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern(pattern.toString())
            .parseDefaulting(ChronoField.YEAR, now.getYear())
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, now.getMonthValue())
            .parseDefaulting(ChronoField.DAY_OF_MONTH, now.getDayOfMonth())
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    @WithSpan("parseInputString")
    public Result<Command, Error> parse(final String input) {
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
        final Result<Arg, Error> argumentResult = getArgument(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(new ParserErr.ArgumentNotFound());

        final Arg arg = argumentResult.unwrap();
        final Option<Opt.Description> descriptionOption = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startOption = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endOption = getOption(tokens, Opt.End.class);

        final String title = arg.value();
        final String description = switch (descriptionOption) {
            case Some(Opt.Description(final String d)) -> d;
            case None<Opt.Description> ignored -> null;
        };
        final LocalDateTime start = switch (startOption) {
            case Some(Opt.Start(final String startStr)) -> switch (parseDate(startStr)) {
                case Ok(final LocalDateTime date) -> date;
                case Err(final Error ignored) -> null;
            };
            case None<Opt.Start> ignored -> null;
        };
        final LocalDateTime end = switch (endOption) {
            case Some(Opt.End(final String endStr)) -> switch (parseDate(endStr)) {
                case Ok(final LocalDateTime date) -> date;
                case Err(final Error ignored) -> null;
            };
            case None<Opt.End> ignored -> null;
        };

        return CreateTodoCmd.of(
                title,
                description,
                start,
                end
        );
    }

    private Result<Command, Error> parseTodoListCommand(final SequencedCollection<Token> tokens) {
        final Option<Set<Arg>> argumentsOption = getOptionalArguments(tokens, Arg.class);

        return switch (argumentsOption) {
            case Some(final Set<Arg> args) -> {
                final Set<Integer> ids = args.stream()
                        .map(a -> Integer.parseInt(a.value()))
                        .collect(Collectors.toSet());

                yield ListTodoCmd.of(ids);
            }
            case None() -> ListTodoCmd.of();
        };
    }

    private Result<Command, Error> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        final Result<Set<Arg>, Error> argumentResult = getArguments(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(argumentResult.error());

        final Set<Arg> args = argumentResult.unwrap();
        final Option<Opt.Title> titleOption = getOption(tokens, Opt.Title.class);
        final Option<Opt.Description> descriptionOption = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startOption = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endOption = getOption(tokens, Opt.End.class);

        final Set<String> filters = args.stream()
                .map(Arg::value)
                .collect(Collectors.toSet());
        final String title = switch (titleOption) {
            case Some(Opt.Title(final String t)) -> t;
            case None<Opt.Title> ignored -> null;
        };
        final String description = switch (descriptionOption) {
            case Some(Opt.Description(final String d)) -> d;
            case None<Opt.Description> ignored -> null;
        };
        final LocalDateTime start = switch (startOption) {
            case Some(Opt.Start(final String startStr)) -> switch (parseDate(startStr)) {
                case Ok(final LocalDateTime date) -> date;
                case Err(final Error ignored) -> null;
            };
            case None<Opt.Start> ignored -> null;
        };
        final LocalDateTime end = switch (endOption) {
            case Some(Opt.End(final String endStr)) -> switch (parseDate(endStr)) {
                case Ok(final LocalDateTime date) -> date;
                case Err(final Error ignored) -> null;
            };
            case None<Opt.End> ignored -> null;
        };

        return UpdateTodoCmd.of(
                filters,
                title,
                description,
                start,
                end
        );
    }

    private Result<Command, Error> parseTodoDeleteCommand(final SequencedCollection<Token> tokens) {
        final Result<Set<Arg>, Error> argumentResult = getArguments(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(argumentResult.error());

        final Set<Arg> args = argumentResult.unwrap();

        // always should be integer, move casting to tokenizer
        final Set<Integer> ids = args.stream()
                .map(Arg::value)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        return DeleteTodoCmd.of(ids);
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

    private static <T> Option<Set<T>> getOptionalArguments(
            final Collection<? extends Token> opts,
            final Class<T> target
    ) {
        final Set<T> args = opts.stream()
                .filter(target::isInstance)
                .map(target::cast)
                .collect(Collectors.toSet());

        if (args.isEmpty())
            return Option.none();

        return Option.over(args);
    }

    private static <T> Result<Set<T>, Error> getArguments(
            final Collection<? extends Token> opts,
            final Class<T> target
    ) {
        final Set<T> args = opts.stream()
                .filter(target::isInstance)
                .map(target::cast)
                .collect(Collectors.toSet());

        if (args.isEmpty())
            return Result.err(new ParserErr.ArgumentNotFound());

        return Result.ok(args);
    }

    private static Result<LocalDateTime, Error> parseDate(final String string) {
        try {
            // todo: make possible to parse with LocalTime
            //          otherwise creation by pure time "12:30" is not possible
            return Result.ok(LocalDateTime.parse(string, formatter));
        } catch (DateTimeParseException e) {
            return Result.err(new ParserErr.DateTimeParseErr(e.getParsedString()));
        }
    }
}