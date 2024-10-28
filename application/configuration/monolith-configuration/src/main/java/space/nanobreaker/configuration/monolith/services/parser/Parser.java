package space.nanobreaker.configuration.monolith.services.parser;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.DeleteTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.EndDateTime;
import space.nanobreaker.configuration.monolith.services.command.ListTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.StartDateTime;
import space.nanobreaker.configuration.monolith.services.command.UpdateTodoCommand;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Arg;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Cmd;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Opt;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Prog;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.Token;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.None;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.option.Some;
import space.nanobreaker.library.result.Err;
import space.nanobreaker.library.result.Ok;
import space.nanobreaker.library.result.Result;
import space.nanobreaker.library.tuple.Tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

    private static final StringBuilder datePattern = new StringBuilder()
            .append("[dd[/][.][-]MM[/][.][-]yyyy]")
            .append("[dd[/][.][-]MM[/][.][-]yy]")
            .append("[dd[/][.][-]MM]")
            .append("[dd]");

    private static final StringBuilder timePattern = new StringBuilder()
            .append("[HH:mm]");

    // todo: migrate to clock service (implement one)
    private static final LocalDateTime now = LocalDateTime.now();
    private static final DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendPattern(datePattern.toString())
            .parseDefaulting(ChronoField.YEAR, now.getYear())
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, now.getMonthValue())
            .parseDefaulting(ChronoField.DAY_OF_MONTH, now.getDayOfMonth())
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();
    private static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
            .appendPattern(timePattern.toString())
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
            default -> Result.err(new ParserError.UnknownProgram());
        };
    }

    private Result<Command, Error> parseTodoProgram(final SequencedCollection<Token> tokens) {
        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Create ignored -> parseTodoCreateCommand(tokens);
            case Cmd.List ignored -> parseTodoListCommand(tokens);
            case Cmd.Update ignored -> parseTodoUpdateCommand(tokens);
            case Cmd.Delete ignored -> parseTodoDeleteCommand(tokens);
            default -> Result.err(new ParserError.UnknownCommand());
        };
    }

    private Result<Command, Error> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        final Result<Arg, Error> argumentResult = getArgument(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(new ParserError.ArgumentNotFound());

        final Arg arg = argumentResult.unwrap();
        final Option<Opt.Description> descriptionTokenOption = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startTokenOption = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endTokenOption = getOption(tokens, Opt.End.class);

        final String title = arg.value();
        final Option<String> descriptionOption = descriptionTokenOption.map(Opt.Description::value);
        final Result<StartDateTime, Error> startResult = startTokenOption
                .map(token -> Parser.parseString(token.value()))
                .map(Parser::getStartDateTime)
                .okOr(new ParserError.EmptyDateTime())
                .flatten();
        final Result<EndDateTime, Error> endResult = endTokenOption
                .map(token -> Parser.parseString(token.value()))
                .map(Parser::getEndDateTime)
                .okOr(new ParserError.EmptyDateTime())
                .flatten();

        if (startResult instanceof Err(
                Error error
        ) && !(error instanceof ParserError.EmptyDateTime))
            return Result.err(error);
        if (endResult instanceof Err(Error error) && !(error instanceof ParserError.EmptyDateTime))
            return Result.err(error);

        final Option<StartDateTime> startOption = startResult.ok();
        final Option<EndDateTime> endOption = endResult.ok();

        return CreateTodoCommand.of(
                title,
                descriptionOption,
                startOption,
                endOption
        );
    }

    private Result<Command, Error> parseTodoListCommand(final SequencedCollection<Token> tokens) {
        final Option<Set<Arg>> argumentsOption = getOptionalArguments(tokens, Arg.class);

        return switch (argumentsOption) {
            case Some(final Set<Arg> args) -> {
                final Set<Integer> ids = args.stream()
                        .map(a -> Integer.parseInt(a.value()))
                        .collect(Collectors.toSet());

                yield ListTodoCommand.of(ids);
            }
            case None() -> ListTodoCommand.of();
        };
    }

    private Result<Command, Error> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        final Option<Set<Arg>> argumentResult = getOptionalArguments(tokens, Arg.class);
        final Option<Opt.Filters> titleFiltersTokenOption = getOption(tokens, Opt.Filters.class);
        final Option<Opt.Title> titleTokenOption = getOption(tokens, Opt.Title.class);
        final Option<Opt.Description> descriptionTokenOption = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startTokenOption = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endTokenOption = getOption(tokens, Opt.End.class);

        final Option<Set<Integer>> idsOption = argumentResult.map(args -> args.stream()
                .map(Arg::value)
                .map(Integer::parseInt)
                .collect(Collectors.toSet()));
        final Option<List<String>> titleFiltersOption = titleFiltersTokenOption
                .map(token -> Arrays.asList(token.value().split(",")));
        final Option<String> titleOption = titleTokenOption.map(Opt.Title::value);
        final Option<String> descriptionOption = descriptionTokenOption.map(Opt.Description::value);
        final Result<StartDateTime, Error> startResult = startTokenOption
                .map(token -> Parser.parseString(token.value()))
                .map(Parser::getStartDateTime)
                .okOr(new ParserError.EmptyDateTime())
                .flatten();
        final Result<EndDateTime, Error> endResult = endTokenOption
                .map(token -> Parser.parseString(token.value()))
                .map(Parser::getEndDateTime)
                .okOr(new ParserError.EmptyDateTime())
                .flatten();

        if (startResult instanceof Err(
                Error error
        ) && !(error instanceof ParserError.EmptyDateTime))
            return Result.err(error);
        if (endResult instanceof Err(Error error) && !(error instanceof ParserError.EmptyDateTime))
            return Result.err(error);

        final Option<StartDateTime> startOption = startResult.ok();
        final Option<EndDateTime> endOption = endResult.ok();

        return UpdateTodoCommand.of(
                idsOption,
                titleFiltersOption,
                titleOption,
                descriptionOption,
                startOption,
                endOption
        );
    }

    private Result<Command, Error> parseTodoDeleteCommand(final SequencedCollection<Token> tokens) {
        final Result<Set<Arg>, Error> argumentResult = getArguments(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(argumentResult.error());

        final Set<Arg> args = argumentResult.unwrap();

        final Set<Integer> ids = args.stream()
                .map(Arg::value)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        return DeleteTodoCommand.of(ids);
    }

    private Result<Command, Error> parseCalendarProgram(final SequencedCollection<Token> tokens) {
        return Result.err(new ParserError.NotSupportedOperation());
    }

    private Result<Command, Error> parseUserProgram(final SequencedCollection<Token> tokens) {
        return Result.err(new ParserError.NotSupportedOperation());
    }

    private static <T> Option<T> getOption(
            final Collection<? extends Token> opts,
            final Class<T> target
    ) {
        final Optional<T> optional = opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast);

        return Option.of(optional);
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
                .orElse(Result.err(new ParserError.ArgumentNotFound()));
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

        return Option.of(args);
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
            return Result.err(new ParserError.ArgumentNotFound());

        return Result.ok(args);
    }

    // todo: figure out a way to simplify this shit
    public static Result<StartDateTime, Error> getStartDateTime(
            final Tuple<Result<LocalDate, Error>, Result<LocalTime, Error>> dateTimeTuple
    ) {
        return switch (dateTimeTuple) {
            case Tuple(Ok(final LocalDate date), Ok(final LocalTime time)) -> {
                yield Result.ok(StartDateTime.of(date, time));
            }
            case Tuple(Ok(final LocalDate date), Err(final Error error)) -> switch (error) {
                case ParserError.EmptyTime ignored -> Result.ok(StartDateTime.of(date));
                default -> Result.err(error);
            };
            case Tuple(Err(final Error error), Ok(final LocalTime time)) -> switch (error) {
                case ParserError.EmptyDate ignored -> Result.ok(StartDateTime.of(time));
                default -> Result.err(error);
            };
            case Tuple(
                    Err(final ParserError.EmptyDate ignored1),
                    Err(final ParserError.EmptyTime ignored2)
            ) -> {
                yield Result.err(new ParserError.EmptyDateTime());
            }
            case Tuple(Err(final Error dateError), Err(final Error ignored)) -> {
                yield Result.err(dateError);
            }
        };
    }

    // todo: figure out a way to simplify this shit
    public static Result<EndDateTime, Error> getEndDateTime(
            final Tuple<Result<LocalDate, Error>, Result<LocalTime, Error>> dateTimeTuple
    ) {
        return switch (dateTimeTuple) {
            case Tuple(Ok(final LocalDate date), Ok(final LocalTime time)) -> {
                yield Result.ok(EndDateTime.of(date, time));
            }
            case Tuple(Ok(final LocalDate date), Err(final Error error)) -> switch (error) {
                case ParserError.EmptyTime ignored -> Result.ok(EndDateTime.of(date));
                default -> Result.err(error);
            };
            case Tuple(Err(final Error error), Ok(final LocalTime time)) -> switch (error) {
                case ParserError.EmptyDate ignored -> Result.ok(EndDateTime.of(time));
                default -> Result.err(error);
            };
            case Tuple(
                    Err(final ParserError.EmptyDate ignored1),
                    Err(final ParserError.EmptyTime ignored2)
            ) -> {
                yield Result.err(new ParserError.EmptyDateTime());
            }
            case Tuple(Err(final Error dateError), Err(final Error ignored)) -> {
                yield Result.err(dateError);
            }
        };
    }

    // todo: figure out a way to simplify this shit
    public static Tuple<Result<LocalDate, Error>, Result<LocalTime, Error>> parseString(
            final String string
    ) {
        final String[] dateTimeParts = string.split(" ", 2);

        if (dateTimeParts.length == 0)
            return new Tuple<>(
                    Result.err(new ParserError.EmptyDate()),
                    Result.err(new ParserError.EmptyTime())
            );

        if (dateTimeParts.length == 1
                && !isDateString(dateTimeParts[0])
                && !isTimeString(dateTimeParts[0])
        )
            return new Tuple<>(
                    Result.err(new ParserError.DateTimeParseError(string)),
                    Result.err(new ParserError.DateTimeParseError(string))
            );

        if (dateTimeParts.length == 1 && isDateString(dateTimeParts[0]))
            return new Tuple<>(
                    Parser.parseDate(dateTimeParts[0]),
                    Result.err(new ParserError.EmptyTime())
            );

        if (dateTimeParts.length == 1 && isTimeString(dateTimeParts[0]))
            return new Tuple<>(
                    Result.err(new ParserError.EmptyDate()),
                    Parser.parseTime(dateTimeParts[0])
            );

        return new Tuple<>(
                Parser.parseDate(dateTimeParts[0]),
                Parser.parseTime(dateTimeParts[1])
        );
    }

    private static Result<LocalDate, Error> parseDate(final String string) {
        try {
            final LocalDate date = LocalDate.parse(string, dateFormatter);
            return Result.ok(date);
        } catch (DateTimeParseException e) {
            return Result.err(new ParserError.DateParseError(e.getParsedString()));
        }
    }

    private static Result<LocalTime, Error> parseTime(final String string) {
        try {
            final LocalTime date = LocalTime.parse(string, timeFormatter);
            return Result.ok(date);
        } catch (DateTimeParseException e) {
            return Result.err(new ParserError.TimeParseError(e.getParsedString()));
        }
    }

    private static boolean isTimeString(final String string) {
        return string.contains(":");
    }

    private static boolean isDateString(final String string) {
        final Set<String> indicators = Set.of(".", "/", "-");
        return indicators.stream().anyMatch(string::contains) || string.length() == 2;
    }
}