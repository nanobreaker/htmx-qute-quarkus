package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.DeleteTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.ListTodoCommand;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private static final String datePattern = new StringBuilder()
            .append("[[dd[/][.][-]MM[/][.][-]yyyy] [HH:mm]]")
            .append("[dd[/][.][-]MM[/][.][-]yyyy]")
            .append("[[dd[/][.][-]MM[/][.][-]yy] [HH:mm]]")
            .append("[dd[/][.][-]MM[/][.][-]yy]")
            .append("[[dd[/][.][-]MM] [HH:mm]]")
            .append("[dd[/][.][-]MM]")
            .append("[[dd] [HH:mm]]")
            .append("[HH:mm]")
            .append("[dd]")
            .toString();

    private final Clock clock;
    private final Tokenizer tokenizer;

    @Inject
    public Parser(
            final Clock clock,
            final Tokenizer tokenizer
    ) {
        this.tokenizer = tokenizer;
        this.clock = clock;
    }

    @WithSpan("parseInputString")
    public Result<Command, Error> parse(final String input) {
        final Result<SequencedCollection<Token>, Error> result = tokenizer.tokenize(input);

        if (result.isErr())
            return Result.err(result.unwrapErr());

        final SequencedCollection<Token> tokens = result.unwrap();
        final Token programToken = tokens.removeFirst();

        return switch (programToken) {
            case Prog.Todo _ -> parseTodoProgram(tokens);
            case Prog.Calendar _ -> parseCalendarProgram(tokens);
            case Prog.User _ -> parseUserProgram(tokens);
            default -> Result.err(new ParserError.UnknownProgram());
        };
    }

    private Result<Command, Error> parseTodoProgram(final SequencedCollection<Token> tokens) {
        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Cmd.Create _ -> parseTodoCreateCommand(tokens);
            case Cmd.List _ -> parseTodoListCommand(tokens);
            case Cmd.Update _ -> parseTodoUpdateCommand(tokens);
            case Cmd.Delete _ -> parseTodoDeleteCommand(tokens);
            default -> Result.err(new ParserError.UnknownCommand());
        };
    }

    private Result<Command, Error> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        final Result<Arg, Error> argumentResult = getArgument(tokens, Arg.class);

        if (argumentResult.isErr())
            return Result.err(new ParserError.ArgumentNotFound());

        final Arg arg = argumentResult.unwrap();
        final Option<Opt.Description> descriptionToken = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startToken = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endToken = getOption(tokens, Opt.End.class);

        final String title = arg.value();
        final Option<String> descriptionOption = descriptionToken
                .map(Opt.Description::value);
        final Option<LocalDateTime> start = startToken
                .flatMap(t -> Option.of(this.parseDateTime(t.value()).ok()));
        final Option<LocalDateTime> end = endToken
                .flatMap(t -> Option.of(this.parseDateTime(t.value()).ok()));

        return CreateTodoCommand.of(
                title,
                descriptionOption,
                start,
                end
        );
    }

    private Result<Command, Error> parseTodoListCommand(final SequencedCollection<Token> tokens) {
        final Option<Set<Arg>> argumentsOption = getOptionalArguments(tokens, Arg.class);

        return switch (argumentsOption) {
            case Some(final Set<Arg> args) -> {
                var ids = args.stream()
                        .map(a -> Integer.parseInt(a.value()))
                        .collect(Collectors.toSet());

                yield ListTodoCommand.of(ids);
            }
            case None() -> ListTodoCommand.of();
        };
    }

    private Result<Command, Error> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        final Option<Set<Arg>> argumentResult = getOptionalArguments(tokens, Arg.class);
        final Option<Opt.Filters> filtersToken = getOption(tokens, Opt.Filters.class);
        final Option<Opt.Title> titleToken = getOption(tokens, Opt.Title.class);
        final Option<Opt.Description> descriptionToken = getOption(tokens, Opt.Description.class);
        final Option<Opt.Start> startToken = getOption(tokens, Opt.Start.class);
        final Option<Opt.End> endToken = getOption(tokens, Opt.End.class);

        final Option<Set<Integer>> ids = argumentResult.map(args -> args.stream()
                .map(Arg::value)
                .map(Integer::parseInt)
                .collect(Collectors.toSet()));
        final Option<List<String>> filters = filtersToken
                .map(token -> Arrays.asList(token.value().split(",")));
        final Option<String> title = titleToken
                .map(Opt.Title::value);
        final Option<String> description = descriptionToken
                .map(Opt.Description::value);
        final Option<LocalDateTime> start = startToken
                .flatMap(t -> Option.of(this.parseDateTime(t.value()).ok()));
        final Option<LocalDateTime> end = endToken
                .flatMap(t -> Option.of(this.parseDateTime(t.value()).ok()));

        return UpdateTodoCommand.of(
                ids,
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
            return Result.err(argumentResult.unwrapErr());

        final Set<Arg> args = argumentResult.unwrap();

        final Set<Integer> ids = args.stream()
                .map(Arg::value)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        return DeleteTodoCommand.of(ids);
    }

    private Result<Command, Error> parseCalendarProgram(final SequencedCollection<Token> ignored) {
        return Result.err(new ParserError.NotSupportedOperation());
    }

    private Result<Command, Error> parseUserProgram(final SequencedCollection<Token> ignored) {
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

    public Result<LocalDateTime, Error> parseDateTime(final String string) {
        final var current = clock.instant().atZone(ZoneId.of("UTC"));
        final var formatter = new DateTimeFormatterBuilder()
                .appendPattern(datePattern)
                .parseDefaulting(ChronoField.YEAR, current.getYear())
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, current.getMonthValue())
                .parseDefaulting(ChronoField.DAY_OF_MONTH, current.getDayOfMonth())
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        try {
            var local = LocalDateTime.parse(string, formatter);
            return Result.ok(local);
        } catch (DateTimeParseException exception) {
            var error = new ParserError.DateParseError(exception.getMessage());
            return Result.err(error);
        }
    }
}