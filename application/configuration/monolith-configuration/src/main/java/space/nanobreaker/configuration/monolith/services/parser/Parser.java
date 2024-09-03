package space.nanobreaker.configuration.monolith.services.parser;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.*;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.*;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;
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

    // todo: migrate to clock service
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
        final Option<Opt.Description> descriptionTokenOption = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startTokenOption = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endTokenOption = getOption(tokens, Opt.End.class);

        final String title = arg.value();
        final Option<String> descriptionOption = descriptionTokenOption.map(Opt.Description::value);
        final Option<StartDateTime> startOption = startTokenOption
                .map(token -> Parser.parseDateTimeString(token.value()))
                .flatMap(Parser::getStartDateTimeOption);
        final Option<EndDateTime> endOption = endTokenOption
                .map(token -> Parser.parseDateTimeString(token.value()))
                .flatMap(Parser::getEndDateTimeOption);

        final CreateTodoCmd.CreateTodoCmdBuilder builder = new CreateTodoCmd.CreateTodoCmdBuilder();
        builder.withTitle(title);
        if (descriptionOption instanceof Some(final String description))
            builder.withDescription(description);
        if (startOption instanceof Some(final StartDateTime start))
            builder.withStart(start);
        if (endOption instanceof Some(final EndDateTime end))
            builder.withEnd(end);

        return builder.build();
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
        final Option<StartDateTime> startOption = startTokenOption
                .map(token -> Parser.parseDateTimeString(token.value()))
                .flatMap(Parser::getStartDateTimeOption);
        final Option<EndDateTime> endOption = endTokenOption
                .map(token -> Parser.parseDateTimeString(token.value()))
                .flatMap(Parser::getEndDateTimeOption);

        final UpdateTodoCmd.UpdateTodoCmdBuilder builder = new UpdateTodoCmd.UpdateTodoCmdBuilder();
        if (idsOption instanceof Some(final Set<Integer> ids))
            builder.withIds(ids);
        if (titleFiltersOption instanceof Some(final List<String> titleFilters))
            builder.withFilters(titleFilters);
        if (titleOption instanceof Some(final String title))
            builder.withTitle(title);
        if (descriptionOption instanceof Some(final String description))
            builder.withDescription(description);
        if (startOption instanceof Some(final StartDateTime start))
            builder.withStart(start);
        if (endOption instanceof Some(final EndDateTime end))
            builder.withEnd(end);

        return builder.build();
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
            return Result.err(new ParserErr.ArgumentNotFound());

        return Result.ok(args);
    }

    private static Option<StartDateTime> getStartDateTimeOption(
            Result<Tuple<Option<LocalDate>, Option<LocalTime>>, Error> tupleResult
    ) {
        return switch (tupleResult) {
            case Ok(
                    Tuple(
                            Some(final LocalDate date), Some(final LocalTime time)
                    )
            ) -> Option.of(StartDateTime.of(date, time));
            case Ok(
                    Tuple(
                            Some(final LocalDate date), None<LocalTime> ignored
                    )
            ) -> {
                final LocalTime time = LocalTime.of(0, 0, 0);
                yield Option.of(StartDateTime.of(date, time));
            }
            case Ok(
                    Tuple(
                            None<LocalDate> ignored, Some(final LocalTime time)
                    )
            ) -> {
                final LocalDate date = LocalDate.now();
                yield Option.of(StartDateTime.of(date, time));
            }
            default -> Option.none();
        };
    }

    private static Option<EndDateTime> getEndDateTimeOption(
            Result<Tuple<Option<LocalDate>, Option<LocalTime>>, Error> tupleResult
    ) {
        return switch (tupleResult) {
            case Ok(
                    Tuple(
                            Some(final LocalDate date), Some(final LocalTime time)
                    )
            ) -> Option.of(EndDateTime.of(date, time));
            case Ok(
                    Tuple(
                            Some(final LocalDate date), None<LocalTime> ignored
                    )
            ) -> {
                final LocalTime time = LocalTime.of(0, 0, 0);
                yield Option.of(EndDateTime.of(date, time));
            }
            case Ok(
                    Tuple(
                            None<LocalDate> ignored, Some(final LocalTime time)
                    )
            ) -> {
                final LocalDate date = LocalDate.now();
                yield Option.of(EndDateTime.of(date, time));
            }
            default -> Option.none();
        };
    }

    // todo: improve method implementation, meditate on how to do better
    private static Result<Tuple<Option<LocalDate>, Option<LocalTime>>, Error> parseDateTimeString(final String dateTimeStr) {
        try {
            final String[] dateTimeParts = dateTimeStr.split(" ", 2);

            if (dateTimeParts.length == 2
                    && isDateString(dateTimeParts[0])
                    && isTimeString(dateTimeParts[1])
            ) {
                final String datePart = dateTimeParts[0];
                final String timePart = dateTimeParts[1];
                final LocalDate date = LocalDate.parse(datePart, dateFormatter);
                final LocalTime time = LocalTime.parse(timePart, timeFormatter);

                return Result.ok(new Tuple<>(
                        Option.of(date),
                        Option.of(time)
                ));
            } else if (isDateString(dateTimeStr)) {
                final LocalDate date = LocalDate.parse(dateTimeStr, dateFormatter);

                return Result.ok(new Tuple<>(
                        Option.of(date),
                        Option.none()
                ));
            } else if (isTimeString(dateTimeStr)) {
                final LocalTime time = LocalTime.parse(dateTimeStr, timeFormatter);

                return Result.ok(new Tuple<>(
                        Option.none(),
                        Option.of(time)
                ));
            }

            return Result.err(new ParserErr.FailedToParseDate(dateTimeStr));
        } catch (DateTimeParseException e) {
            return Result.err(new ParserErr.FailedToParseDate(e.getParsedString()));
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