package space.nanobreaker.configuration.monolith.services.parser;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.Command.Todo.Create;
import space.nanobreaker.configuration.monolith.services.tokenizer.KEYWORD;
import space.nanobreaker.configuration.monolith.services.tokenizer.OPTION;
import space.nanobreaker.configuration.monolith.services.tokenizer.Token;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.None;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.option.Some;
import space.nanobreaker.library.tuple.Pair;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

import static io.github.dcadea.jresult.Result.err;
import static io.github.dcadea.jresult.Result.ok;
import static space.nanobreaker.configuration.monolith.services.command.Command.Todo.Delete;
import static space.nanobreaker.configuration.monolith.services.command.Command.Todo.List;
import static space.nanobreaker.configuration.monolith.services.command.Command.Todo.Update;
import static space.nanobreaker.library.option.Option.none;
import static space.nanobreaker.library.option.Option.some;

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
        var tokens = tokenizer.tokenize(input);

        try {
            var programToken = tokens.removeFirst();
            return switch (programToken) {
                case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> ok(new Command.Help());
                case Token.Keyword(var keyword) when keyword == KEYWORD.TODO -> parseTodoProgram(tokens);
                case Token.Keyword(var keyword) when keyword == KEYWORD.CALENDAR -> parseCalendarProgram(tokens);
                case Token.Keyword(var keyword) when keyword == KEYWORD.USER -> parseUserProgram(tokens);
                default -> err(new ParserError.UnknownProgram());
            };
        } catch (final NoSuchElementException exception) {
            return err(new ParserError.Empty());
        }
    }

    private Result<Command, Error> parseTodoProgram(final SequencedCollection<Token> tokens) {
        var commandToken = tokens.removeFirst();

        return switch (commandToken) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> ok(new Command.Todo.Help());
            case Token.Keyword(var keyword) when keyword == KEYWORD.CREATE -> parseTodoCreateCommand(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.LIST -> parseTodoListCommand(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.UPDATE -> parseTodoUpdateCommand(tokens);
            case Token.Keyword(var keyword) when keyword == KEYWORD.DELETE -> parseTodoDeleteCommand(tokens);
            default -> err(new ParserError.UnknownCommand());
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
        Create.Default build() { return new Create.Default(title, description, start, end); }
    }
    // @formatter:on

    private Result<Command, Error> parseTodoCreateCommand(final SequencedCollection<Token> tokens) {
        var subcommand = tokens.removeFirst();

        return switch (subcommand) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> {
                yield ok(new Create.Help());
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
                } else if (startOptResult instanceof Some(Err(ParserError.DateParseError parseError))) {
                    yield err(parseError);
                }

                if (endOptResult instanceof Some(Ok(var end))) {
                    builder.withEnd(some(end));
                } else if (endOptResult instanceof Some(Err(ParserError.DateParseError parseError))) {
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
                yield ok(new List.Help());
            }
            case Token.Keyword(var keyword) when keyword == KEYWORD.ALL -> {
                yield ok(new List.All());
            }
            default -> {
                var args = findArgs(tokens);
                var option = some(findOption(tokens, OPTION.FILTER));

                yield switch (option) {
                    case Some(var filter) when args.isEmpty() -> {
                        yield ok(new List.ByFilters(Set.of(filter)));
                    }
                    case Some(var filter) -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        yield ok(new List.ByIdsAndFilters(ids, Set.of(filter)));
                    }
                    case None() when args.isEmpty() -> {
                        yield err(new ParserError.ArgumentNotFound());
                    }
                    case None() -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        yield ok(new List.ByIds(ids));
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
        Update.Payload build() { return new Update.Payload(title, description, start, end); }
    }
    // @formatter:on

    private Result<Command, Error> parseTodoUpdateCommand(final SequencedCollection<Token> tokens) {
        var subcommand = tokens.getFirst();

        return switch (subcommand) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> {
                yield ok(new Update.Help());
            }
            default -> {
                var args = findArgs(tokens);
                var filterOption = some(findOption(tokens, OPTION.FILTER));

                if (args.isEmpty() && filterOption.isNone()) {
                    yield err(new ParserError.ArgumentOrFilterNotFound());
                }

                var titleOpt = some(findOption(tokens, OPTION.TITLE));
                var descriptionOpt = some(findOption(tokens, OPTION.DESCRIPTION));
                var startOptResult = some(findOption(tokens, OPTION.START).map(this::parseDateTime));
                var endOptResult = some(findOption(tokens, OPTION.END).map(this::parseDateTime));

                var paylodBuilder = new TodoUpdateCommandPaylodBuilder();
                paylodBuilder.withTitle(titleOpt);
                paylodBuilder.withDescription(descriptionOpt);

                if (startOptResult instanceof Some(Ok(LocalDateTime start))) {
                    paylodBuilder.withStart(some(start));
                } else if (startOptResult instanceof Some(Err(ParserError.DateParseError parseError))) {
                    yield err(parseError);
                }

                if (endOptResult instanceof Some(Ok(LocalDateTime end))) {
                    paylodBuilder.withEnd(some(end));
                } else if (endOptResult instanceof Some(Err(ParserError.DateParseError parseError))) {
                    yield err(parseError);
                }

                yield switch (filterOption) {
                    case Some(var filter) when args.isEmpty() -> {
                        var command = new Update.ByFilters(Set.of(filter), paylodBuilder.build());
                        yield ok(command);
                    }
                    case Some(var filter) -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        var command = new Update.ByIdsAndFilters(ids, Set.of(filter), paylodBuilder.build());
                        yield ok(command);
                    }
                    case None() when args.isEmpty() -> {
                        yield err(new ParserError.ArgumentNotFound());
                    }
                    case None() -> {
                        var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                        var command = new Update.ByIds(ids, paylodBuilder.build());
                        yield ok(command);
                    }
                };
            }
        };
    }

    private Result<Command, Error> parseTodoDeleteCommand(final SequencedCollection<Token> tokens) {
        var subcommand = tokens.getFirst();

        return switch (subcommand) {
            case Token.Keyword(var keyword) when keyword == KEYWORD.HELP -> {
                yield ok(new Delete.Help());
            }
            case Token.Keyword(var keyword) when keyword == KEYWORD.ALL -> {
                yield ok(new Delete.All());
            }
            default -> {
                var args = findArgs(tokens);

                if (args.isEmpty()) {
                    yield err(new ParserError.ArgumentNotFound());
                } else {
                    var ids = args.stream().map(Integer::parseInt).collect(Collectors.toSet());
                    yield ok(new Delete.ByIds(ids));
                }
            }
        };
    }

    private Result<Command, Error> parseCalendarProgram(final SequencedCollection<Token> ignored) {
        return err(new ParserError.NotSupportedOperation());
    }

    private Result<Command, Error> parseUserProgram(final SequencedCollection<Token> ignored) {
        return err(new ParserError.NotSupportedOperation());
    }

    private static Optional<String> findOption(
            final SequencedCollection<? extends Token> tokens,
            final OPTION target
    ) {
        return tokens.stream()
                .gather(Gatherers.windowSliding(2))
                .gather(Gatherers.fold(
                        Option::<String>none,
                        (text, window) -> switch (Pair.of(window.getFirst(), window.getLast())) {
                            case Pair(Token.Option(var opt), Token.Text(var _text)) when opt == target -> some(_text);
                            default -> text;
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

    public Result<LocalDateTime, Error> parseDateTime(final String string) {
        var current = clock.instant().atZone(ZoneId.of("UTC"));
        var formatter = new DateTimeFormatterBuilder()
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