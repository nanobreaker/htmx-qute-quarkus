package space.nanobreaker.configuration.monolith.cli.parser;

import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.configuration.monolith.cli.command.*;
import space.nanobreaker.configuration.monolith.extension.Ok;
import space.nanobreaker.configuration.monolith.extension.Result;
import space.nanobreaker.configuration.monolith.extension.Tuple;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommandParser {

    /*
        structure of commands

        todo
            create
                options
                    description
                    start
                    end
            list
            update
                options
                    description
                    start
                    end
            delete
                argument
                    id
        user
            show

        calendar
            show
     */

    // @formatter:off
    sealed interface Error {}
    record TokenizerError(String description) implements Error {}

    sealed interface Spec permits CalendarShow,TodoCreate,TodoDelete,TodoList,TodoUpdate,Unknown,UserShow { }
    record TodoCreate(Set<Option> options) implements Spec { }
    record TodoList() implements Spec { }
    record TodoUpdate(Set<Option> options) implements Spec { }
    record TodoDelete(Set<Argument> arguments) implements Spec { }
    record UserShow() implements Spec { }
    record CalendarShow() implements Spec { }
    record Unknown(Program program, Command command) implements Spec { }

    sealed interface Token { }

    record Argument(String value) implements Token { }

    sealed interface Option extends Token { }
    record Description(String value) implements Option { }

    record Start(String value) implements Option {
        // todo: rework using Result, handle unchecked exception
        LocalDateTime toDate() {
            return LocalDateTime.parse(value);
        }
    }
    record End(String value) implements Option {
        // todo: rework using Result, handle unchecked exception
        LocalDateTime toDate() {
            return LocalDateTime.parse(value);
        }
    }

    sealed interface Program extends Token { }
    record Todo() implements Program { }
    record Calendar() implements Program { }

    record User() implements Program { }
    sealed interface Command extends Token { }
    record Create() implements Command { }
    record List() implements Command { }
    record Update() implements Command { }
    record Delete() implements Command { }

    record Show() implements Command { }
    // @formatter:on

    // todo: rework tokenizer using FINITE STATE MACHINE
    private Spec tokenize(final String input) {
        final String[] primaryTokens = input.split(" ", 3);
        final String pts = primaryTokens[0];
        final String cts = primaryTokens[1];

        final Result<Program, TokenizerError> program = switch (pts) {
            case "todo" -> Result.ok(new Todo());
            case "user" -> Result.ok(new User());
            case "calendar" -> Result.ok(new Calendar());
            default -> Result.err(new TokenizerError("Unknown program"));
        };

        final Result<Command, TokenizerError> command = switch (cts) {
            case "create" -> Result.ok(new Create());
            case "list" -> Result.ok(new List());
            case "update" -> Result.ok(new Update());
            case "delete" -> Result.ok(new Delete());
            case "show" -> Result.ok(new Show());
            default -> Result.err(new TokenizerError("Unknown command"));
        };

        // -d"test description"    -s"11/02/13"     argument
        // DescriptionOption        StartOption     Argument
        final String[] remainingTokens = primaryTokens[2].split(" ");
        final Pattern optionPattern = Pattern.compile("-([a-zA-Z])\"([^\"\\s]*)\"");
        final Set<Option> options = Arrays.stream(remainingTokens)
                .filter(t -> t.matches(optionPattern.pattern()))
                .map(optionPattern::matcher)
                .map(m -> switch (m.group(1).charAt(0)) {
                    case 'd' -> new Description(m.group(2));
                    case 's' -> new Start(m.group(2));
                    case 'e' -> new End(m.group(2));
                    default -> throw new IllegalStateException("unknown option " + m.group(1));
                })
                .collect(Collectors.toSet());

        // todo: rework options extractor using Result<Option, TokenizerError>
        /*
        final Set<Result<Option, TokenizerError>> options = Arrays.stream(remainingTokens)
                .filter(t -> t.matches(optionPattern.pattern()))
                .map(optionPattern::matcher)
                .map(matcher -> switch (matcher.group(1).charAt(0)) {
                    case 'd' -> Result.<Option, TokenizerError>ok(new Description(matcher.group(2)));
                    case 's' -> Result.<Option, TokenizerError>ok(new Start(matcher.group(2)));
                    case 'e' -> Result.<Option, TokenizerError>ok(new End(matcher.group(2)));
                    default ->
                            Result.<Option, TokenizerError>err(new TokenizerError("Unknown option " + matcher.group(1)));
                })
                .collect(Collectors.toSet());
        */

        // todo: implement parsing of arguments

        // todo: rework using map and mapErr
        final Result<Tuple<Program, Command>, TokenizerError> tuple = Result.merge(program, command);
        return switch (tuple) {
            case Ok(Tuple(Todo _, Create _)) -> new TodoCreate(options);
            case Ok(Tuple(Todo _, List _)) -> new TodoList();
            case Ok(Tuple(Todo _, Update _)) -> new TodoUpdate(options);
            case Ok(Tuple(Todo _, Delete _)) -> new TodoDelete(Collections.emptySet());
            case Ok(Tuple(User _, Show _)) -> new UserShow();
            case Ok(Tuple(Calendar _, Show _)) -> new CalendarShow();
            default -> new Unknown(program.unwrap(), command.unwrap());
        };
    }

    public Result<CliCommand, Exception> parse(final String input) {
        final Spec spec = tokenize(input);
        return switch (spec) {
            case TodoCreate(Set<Option> opts) when opts.isEmpty() -> Result.err(new IllegalStateException());
            case TodoCreate(Set<Option> opts) -> getOpt(opts, Description.class)
                    .map(Description::value)
                    .map(description -> Result.merge(getStart(opts), getEnd(opts))
                            .map(tuple -> {
                                final Optional<Start> start = tuple.first();
                                final Optional<End> end = tuple.second();
                                if (start.isPresent() && end.isPresent()) {
                                    return new CreateTodoCommand(description, Range.of(start.get().toDate(), end.get().toDate()));
                                } else if (start.isPresent()) {
                                    return new CreateTodoCommand(description, Range.from(start.get().toDate()));
                                } else if (end.isPresent()) {
                                    return new CreateTodoCommand(description, Range.until(end.get().toDate()));
                                } else {
                                    return new CreateTodoCommand(description, Range.of(null, null));
                                }
                            }))
                    .map(result -> result.unwrap());

            case TodoList() -> Result.ok(new ListTodoCommand());
            case TodoUpdate(Set<Option> opts) when opts.isEmpty() -> Result.err(new IllegalStateException());
            case TodoUpdate(Set<Option> opts) -> {
                // todo: rework as TodoCreate command
                final Optional<Description> description = getOpOpt(opts, Description.class);
                final Optional<Start> start = getOpOpt(opts, Start.class);
                final Optional<End> end = getOpOpt(opts, End.class);
                yield Result.ok(new UpdateTodoCommand(Set.of(), description.map(Description::value).orElse(null), start.map(Start::value).map(LocalDateTime::parse).orElse(null), end.map(End::value).map(LocalDateTime::parse).orElse(null)));
            }
            case TodoDelete(Set<Argument> args) when args.isEmpty() -> Result.err(new IllegalStateException());
            case TodoDelete(Set<Argument> _) -> Result.ok(new DeleteTodoCommand(Set.of()));
            case UserShow() -> Result.ok(new UserShowCommand());
            case CalendarShow() -> Result.ok(new CalendarShowCommand());
            case Unknown(Program p, Command c) -> Result.err(new IllegalStateException("not supported"));
        };
    }

    private static Result<Optional<Start>, Exception> getStart(Set<Option> opts) {
        for (var opt : opts) {
            if (opt instanceof Start start) return Result.ok(Optional.of(start));
        }
        return Result.ok(Optional.empty());
    }

    private static Result<Optional<End>, Exception> getEnd(Set<Option> opts) {
        for (var opt : opts) {
            if (opt instanceof End end) return Result.ok(Optional.of(end));
        }
        return Result.ok(Optional.empty());
    }

    private static <T extends Option> Optional<T> getOpOpt(Set<Option> opts, Class<T> target) {
        return opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast);
    }

    private static <T extends Option> Result<T, Exception> getOpt(Set<Option> opts, Class<T> target) {
        return opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast)
                .map(Result::<T, Exception>ok)
                .orElse(Result.err(new IllegalStateException()));
    }
}
