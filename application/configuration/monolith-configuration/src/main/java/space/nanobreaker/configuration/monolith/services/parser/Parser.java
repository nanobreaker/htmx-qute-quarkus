package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.tokenizer.Token;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.core.domain.v1.todo.TodoError;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.None;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.option.Some;
import space.nanobreaker.library.tuple.Tuple;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.dcadea.jresult.Result.err;
import static io.github.dcadea.jresult.Result.ok;

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
            return err(result.unwrapErr());

        final SequencedCollection<Token> tokens = result.unwrap();
        final Token programToken = tokens.removeFirst();

        return switch (programToken) {
            case Token.Prog.Todo _ -> parseTodoProgram(tokens);
            case Token.Prog.Calendar _ -> parseCalendarProgram(tokens);
            case Token.Prog.User _ -> parseUserProgram(tokens);
            default -> err(new ParserError.UnknownProgram());
        };
    }

    private Result<Command, Error> parseTodoProgram(final SequencedCollection<Token> tokens) {
        final Token commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Token.Cmd.Create _ -> parseTodoCreateCommand(tokens);
            case Token.Cmd.List _ -> parseTodoListCommand(tokens);
            case Token.Cmd.Update _ -> parseTodoUpdateCommand(tokens);
            case Token.Cmd.Delete _ -> parseTodoDeleteCommand(tokens);
            default -> err(new ParserError.UnknownCommand());
        };
    }

    private Result<Command, Error> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        final Result<Token.Arg, Error> argumentResult = getArg(tokens);
        if (argumentResult.isErr()) {
            return err(new ParserError.ArgumentNotFound());
        }

        final Token.Arg arg = argumentResult.unwrap();
        final Option<Token.Opt.Description> descriptionToken = findToken(tokens, Token.Opt.Description.class);
        final Option<Token.Opt.Start> startToken = findToken(tokens, Token.Opt.Start.class);
        final Option<Token.Opt.End> endToken = findToken(tokens, Token.Opt.End.class);

        final String title = arg.value();
        final Option<String> descriptionOption = descriptionToken
                .map(Token.Opt.Description::value);
        final Result<Option<LocalDateTime>, Error> startResult = startToken
                .map(token -> this.parseDateTime(token.value()).map(Option::some))
                .orElse(ok(Option.none()));
        final Result<Option<LocalDateTime>, Error> endResult = endToken
                .map(token -> this.parseDateTime(token.value()).map(Option::some))
                .orElse(ok(Option.none()));

        if (startResult.isErr()) {
            return err(startResult.unwrapErr());
        }
        if (endResult.isErr()) {
            return err(endResult.unwrapErr());
        }

        final var start = startResult.unwrap();
        final var end = endResult.unwrap();
        final var command = new Command.Todo.Create.Default(title, descriptionOption, start, end);

        return ok(command);
    }

    private Result<Command, Error> parseTodoListCommand(final SequencedCollection<Token> tokens) {
        final Set<Token.Arg> args = getArgs(tokens);

        if (args.isEmpty()) {
            var command = new Command.Todo.List.All();
            return ok(command);
        }

        var ids = args.stream()
                .map(a -> Integer.parseInt(a.value()))
                .collect(Collectors.toSet());
        var command = new Command.Todo.List.ByIds(ids);

        return ok(command);
    }

    private Result<Command, Error> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        final Set<Token.Arg> args = getArgs(tokens);
        final Option<Token.Opt.Filters> filtersToken = findToken(tokens, Token.Opt.Filters.class);
        final Option<Token.Opt.Title> titleToken = findToken(tokens, Token.Opt.Title.class);
        final Option<Token.Opt.Description> descriptionToken = findToken(tokens, Token.Opt.Description.class);
        final Option<Token.Opt.Start> startToken = findToken(tokens, Token.Opt.Start.class);
        final Option<Token.Opt.End> endToken = findToken(tokens, Token.Opt.End.class);

        final Set<Integer> ids = args.stream()
                .map(Token.Arg::value)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        final Option<Set<String>> filters = filtersToken
                .map(token -> Set.of(token.value().split(",")));
        final Option<String> title = titleToken
                .map(Token.Opt.Title::value);
        final Option<String> description = descriptionToken
                .map(Token.Opt.Description::value);
        final Result<Option<LocalDateTime>, Error> startResult = startToken
                .map(token -> this.parseDateTime(token.value()).map(Option::some))
                .orElse(ok(Option.none()));
        final Result<Option<LocalDateTime>, Error> endResult = endToken
                .map(token -> this.parseDateTime(token.value()).map(Option::some))
                .orElse(ok(Option.none()));

        if (startResult.isErr()) {
            return err(startResult.unwrapErr());
        }
        if (endResult.isErr()) {
            return err(endResult.unwrapErr());
        }

        final var start = startResult.unwrap();
        final var end = endResult.unwrap();
        final var payload = new Command.Todo.Update.Payload(title, description, start, end);
        final var tuple = new Tuple<>(ids, filters);

        return switch (tuple) {
            case Tuple(var idz, Some(var filterz)) when !idz.isEmpty() -> {
                var command = new Command.Todo.Update.ByIdsAndFilters(idz, filterz, payload);
                yield ok(command);
            }
            case Tuple(var idz, None()) when !idz.isEmpty() -> {
                var command = new Command.Todo.Update.ByIds(idz, payload);
                yield ok(command);
            }
            case Tuple(var _, Some(var filterz)) -> {
                var command = new Command.Todo.Update.ByFilters(filterz, payload);
                yield ok(command);
            }
            case Tuple(var _, None()) -> {
                yield err(new TodoError.Unknown());
            }
        };
    }

    private Result<Command, Error> parseTodoDeleteCommand(final SequencedCollection<Token> tokens) {
        final Option<Token.SubCmd> subCmd = findToken(tokens, Token.SubCmd.class);
        final Set<Token.Arg> args = getArgs(tokens);
        final var tuple = new Tuple<>(subCmd, args);

        return switch (tuple) {
            case Tuple(Some(_), Set<Token.Arg> ids) when !ids.isEmpty() -> {
                var error = new TodoError.NotFound();
                yield err(error);
            }
            case Tuple(Some(_), Set<Token.Arg> _) -> {
                var command = new Command.Todo.Delete.All();
                yield ok(command);
            }
            case Tuple(None(), Set<Token.Arg> ids) when !ids.isEmpty() -> {
                var idz = args.stream()
                        .map(Token.Arg::value)
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());

                var command = new Command.Todo.Delete.ByIds(idz);
                yield ok(command);
            }
            case Tuple(None(), Set<Token.Arg> _) -> {
                var error = new TodoError.Unknown();
                yield err(error);
            }
        };
    }

    private Result<Command, Error> parseCalendarProgram(final SequencedCollection<Token> ignored) {
        return err(new ParserError.NotSupportedOperation());
    }

    private Result<Command, Error> parseUserProgram(final SequencedCollection<Token> ignored) {
        return err(new ParserError.NotSupportedOperation());
    }

    private static <T> Option<T> findToken(
            final Collection<? extends Token> tokens,
            final Class<T> target
    ) {
        final Optional<T> optional = tokens.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast);

        return Option.some(optional);
    }

    private static Result<Token.Arg, Error> getArg(final Collection<? extends Token> tokens) {
        return tokens.stream()
                .filter(t -> t instanceof Token.Arg)
                .findFirst()
                .map(t -> (Token.Arg) t)
                .map(Result::<Token.Arg, Error>ok)
                .orElse(err(new ParserError.ArgumentNotFound()));
    }

    private static Set<Token.Arg> getArgs(final Collection<? extends Token> tokens) {
        return tokens.stream()
                .filter(t -> t instanceof Token.Arg)
                .map(t -> (Token.Arg) t)
                .collect(Collectors.toUnmodifiableSet());
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
            return ok(local);
        } catch (DateTimeParseException exception) {
            var error = new ParserError.DateParseError(exception.getMessage());
            return err(error);
        }
    }
}