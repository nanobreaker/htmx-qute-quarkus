package dev.thatwhichis.rest.adapter.cli.parser;

import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.None;
import dev.thatwhichis.library.option.Option;
import dev.thatwhichis.library.option.Some;
import dev.thatwhichis.library.tuple.Pair;
import dev.thatwhichis.rest.adapter.cli.Command;
import dev.thatwhichis.rest.adapter.cli.tokenizer.KEYWORD;
import dev.thatwhichis.rest.adapter.cli.tokenizer.OPTION;
import dev.thatwhichis.rest.adapter.cli.tokenizer.Token;
import dev.thatwhichis.rest.adapter.cli.tokenizer.Tokenizer;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

import static dev.thatwhichis.library.option.Option.none;
import static dev.thatwhichis.library.option.Option.some;
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

    private final Tokenizer tokenizer;

    @Inject
    public Parser(final Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @WithSpan("parse")
    public Result<Command, Error> parse(final String input) {
        var tokens = tokenizer.tokenize(input);

        if (tokens.isEmpty())
            return err(new ParserError.Empty());

        var programToken = tokens.removeFirst();
        return switch (programToken) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> ok(new Command.Help());
            case Token.Keyword(var keyword) when keyword == KEYWORD.TODO -> parseTodoProgram(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.CALENDAR -> parseCalendarProgram(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.USER -> parseUserProgram(tokens);
            case Token.Keyword(var keyword) -> err(new ParserError.NotProgram(keyword.toString()));
            case Token.Text _, Token.Option _ -> err(new ParserError.NotProgram(programToken.toString()));
            case Token.Unknown(var unknown) -> err(new ParserError.UnknownToken(unknown));
        };
    }

    private Result<Command, Error> parseTodoProgram(final SequencedCollection<Token> tokens) {
        var commandToken = tokens.removeFirst();
        return switch (commandToken) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> ok(new Command.Todo.Help());
            case Token.Keyword(var keyword) when keyword == KEYWORD.CREATE -> parseTodoCreateCommand(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.LIST -> parseTodoListCommand(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.UPDATE -> parseTodoUpdateCommand(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.DELETE -> parseTodoDeleteCommand(tokens);
            default -> err(new ParserError.UnknownCommand(commandToken.toString()));
        };
    }

    // @formatter:off
    private static class TodoCreateCommandBuilder {
        private final String title;
        private Option<String> description = none();
        private Option<LocalDateTime> start = none();
        private Option<LocalDateTime> end = none();
        TodoCreateCommandBuilder(String title) { this.title = title; }
        void withDescription(Option<String> description) { this.description = description; }
        void withStart(Option<LocalDateTime> start) { this.start = start; }
        void withEnd(Option<LocalDateTime> end) { this.end = end; }
        Command.Todo.Create.Default build() { return new Command.Todo.Create.Default(title, description, start, end); }
    }
    // @formatter:on

    private Result<Command, Error> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        var subcommand = tokens.removeFirst();
        return switch (subcommand) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> {
                yield ok(new Command.Todo.Create.Help());
            }
            case Token.Text(var title) -> {
                var isSingleArg = findArgs(tokens).isEmpty();
                if (!isSingleArg) {
                    yield err(new ParserError.RedundantArgs());
                }
                var descriptionOpt = some(findOption(tokens, OPTION.DESCRIPTION));
                var startOptResult = some(findOption(tokens, OPTION.START).map(this::parseDateTime));
                var endOptResult = some(findOption(tokens, OPTION.END).map(this::parseDateTime));
                var builder = new TodoCreateCommandBuilder(title);
                builder.withDescription(descriptionOpt);
                if (startOptResult instanceof Some(Ok(var start))) {
                    builder.withStart(some(start));
                } else if (startOptResult instanceof Some(Err(var parseError))) {
                    yield err(parseError);
                }
                if (endOptResult instanceof Some(Ok(var end))) {
                    builder.withEnd(some(end));
                } else if (endOptResult instanceof Some(Err(var parseError))) {
                    yield err(parseError);
                }
                var command = builder.build();
                yield ok(command);
            }
            default -> {
                yield err(new ParserError.ArgumentNotFound());
            }
        };
    }

    private Result<Command, Error> parseTodoListCommand(final SequencedCollection<Token> tokens) {
        var subcommand = tokens.getFirst();
        return switch (subcommand) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> {
                yield ok(new Command.Todo.List.Help());
            }
            case Token.Keyword(var keyword) when keyword == KEYWORD.ALL -> {
                yield ok(new Command.Todo.List.All());
            }
            default -> {
                var args = findArgs(tokens);
                var option = some(findOption(tokens, OPTION.FILTER));
                yield switch (option) {
                    case Some(var filter) when args.isEmpty() -> {
                        yield ok(new Command.Todo.List.ByFilters(Set.of(filter)));
                    }
                    case Some(var filter) -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        yield ok(new Command.Todo.List.ByIdsAndFilters(ids, Set.of(filter)));
                    }
                    case None() when args.isEmpty() -> {
                        yield err(new ParserError.ArgumentNotFound());
                    }
                    case None() -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        yield ok(new Command.Todo.List.ByIds(ids));
                    }
                };
            }
        };
    }

    // @formatter:off
    private static class TodoUpdateCommandPaylodBuilder {
        private Option<String> title = none();
        private Option<String> description = none();
        private Option<LocalDateTime> start = none();
        private Option<LocalDateTime> end = none();
        TodoUpdateCommandPaylodBuilder() { }
        void withTitle(Option<String> title) { this.title = title; }
        void withDescription(Option<String> description) { this.description = description; }
        void withStart(Option<LocalDateTime> start) { this.start = start; }
        void withEnd(Option<LocalDateTime> end) { this.end = end; }
        Command.Todo.Update.Payload build() { return new Command.Todo.Update.Payload(title, description, start, end); }
    }
    // @formatter:on

    private Result<Command, Error> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        var subcommand = tokens.getFirst();
        return switch (subcommand) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> {
                yield ok(new Command.Todo.Update.Help());
            }
            default -> {
                var args = findArgs(tokens);
                var filterOpt = some(findOption(tokens, OPTION.FILTER));
                if (args.isEmpty() && filterOpt.isNone()) {
                    yield err(new ParserError.ArgumentOrFilterNotFound());
                }
                var titleOpt = some(findOption(tokens, OPTION.TITLE));
                var descriptionOpt = some(findOption(tokens, OPTION.DESCRIPTION));
                var startOptResult = some(findOption(tokens, OPTION.START).map(this::parseDateTime));
                var endOptResult = some(findOption(tokens, OPTION.END).map(this::parseDateTime));
                var payloadBuilder = new TodoUpdateCommandPaylodBuilder();

                payloadBuilder.withTitle(titleOpt);
                payloadBuilder.withDescription(descriptionOpt);

                if (startOptResult instanceof Some(Ok(LocalDateTime start))) {
                    payloadBuilder.withStart(some(start));
                } else if (startOptResult instanceof Some(Err(var parseError))) {
                    yield err(parseError);
                }
                if (endOptResult instanceof Some(Ok(LocalDateTime end))) {
                    payloadBuilder.withEnd(some(end));
                } else if (endOptResult instanceof Some(Err(var parseError))) {
                    yield err(parseError);
                }
                yield switch (filterOpt) {
                    case Some(var filter) when args.isEmpty() -> {
                        var command = new Command.Todo.Update.ByFilters(Set.of(filter), payloadBuilder.build());
                        yield ok(command);
                    }
                    case Some(var filter) -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        var command = new Command.Todo.Update.ByIdsAndFilters(ids, Set.of(filter), payloadBuilder.build());
                        yield ok(command);
                    }
                    case None() when args.isEmpty() -> {
                        yield err(new ParserError.ArgumentNotFound());
                    }
                    case None() -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        var command = new Command.Todo.Update.ByIds(ids, payloadBuilder.build());
                        yield ok(command);
                    }
                };
            }
        };
    }

    private Result<Command, Error> parseTodoDeleteCommand(final SequencedCollection<Token> tokens) {
        var subcommand = tokens.getFirst();
        return switch (subcommand) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> ok(new Command.Todo.Delete.Help());
            case Token.Keyword(var keyword) when keyword == KEYWORD.ALL -> ok(new Command.Todo.Delete.All());
            default -> {
                var args = findArgs(tokens);
                if (args.isEmpty()) {
                    yield err(new ParserError.ArgumentNotFound());
                } else {
                    var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                    yield ok(new Command.Todo.Delete.ByIds(ids));
                }
            }
        };
    }

    private Result<Command, Error> parseCalendarProgram(final SequencedCollection<Token> ignored) {
        return err(new ParserError.NotSupportedOperation());
    }

    private Result<Command, Error> parseUserProgram(final SequencedCollection<Token> tokens) {
        var commandToken = tokens.removeFirst();
        return switch (commandToken) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> ok(new Command.User.Help());
            case Token.Keyword(var keyword) when keyword == KEYWORD.SHOW -> ok(new Command.User.Show());
            default -> err(new ParserError.UnknownCommand(commandToken.toString()));
        };
    }

    private static Optional<String> findOption(SequencedCollection<Token> tokens, OPTION target) {
        return tokens.stream()
                .gather(Gatherers.windowSliding(2))
                .gather(Gatherers.fold(
                        Option::<String>none,
                        (value, window) -> switch (Pair.of(window.getFirst(), window.getLast())) {
                            case Pair(Token.Option(var opt), Token.Text(var text)) when opt == target -> some(text);
                            default -> value;
                        }
                ))
                .filter(Option::isSome)
                .map(Option::value)
                .findFirst();
    }

    private static Set<String> findArgs(final SequencedCollection<? extends Token> tokens) {
        return tokens.stream()
                .takeWhile(token -> token instanceof Token.Text)
                .map(token -> (Token.Text) token)
                .map(Token.Text::text)
                .collect(Collectors.toSet());
    }

    private Result<LocalDateTime, Error> parseDateTime(final String string) {
        var current = Clock.systemUTC().instant().atZone(ZoneId.of("UTC"));
        var formatter = new DateTimeFormatterBuilder()
                .appendPattern(datePattern)
                .parseDefaulting(ChronoField.YEAR_OF_ERA, current.getYear())
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