package space.nanobreaker.configuration.monolith.cli.parser;

import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.configuration.monolith.cli.command.*;
import space.nanobreaker.configuration.monolith.extension.Result;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommandParser {

    // @formatter:off
    sealed interface Token { }

    public record Argument(String value) implements Token {
    }

    sealed interface Option extends Token {
    }
    record Description(String value) implements Option {
    }
    record Start(String value) implements Option { }
    record End(String value) implements Option { }

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

    record Spec(Program program, Command command, Set<Option> opts, Set<Argument> args) {
    }
    // @formatter:on

    private Spec tokenize(final String input) {
        final String[] primaryTokens = input.split(" ", 3);
        final String pts = primaryTokens[0];
        final String cts = primaryTokens[1];

        
        // todo replace strings with enums
        final Program program = switch (pts) {
            case "todo" -> new Todo();
            case "user" -> new User();
            case "calendar" -> new Calendar();
            default -> throw new IllegalStateException("unknown program " + pts);
        };

        // todo replace strings with enums
        final Command command = switch (cts) {
            case "create" -> new Create();
            case "list" -> new List();
            case "update" -> new Update();
            case "delete" -> new Delete();
            case "show" -> new Show();
            default -> throw new IllegalStateException("unknown command  " + pts);
        };

        // -d"test description"    -s"11/02/13"   argument    second      bitch
        // DescriptionOption        StartOption     Argument    Argument    Argument
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

        // extract keys
        //


        /* TODO
         *   Implement parsing of arguments and options
         * */

        return new Spec(program, command, Set.of(new Description("hello world")), Set.of());
    }

    public Result<CliCommand, Exception> parse(final String input) {
        try {
            final Spec spec = tokenize(input);
            final CliCommand command = switch (spec) {
                case Spec(Todo _, Create _, Set<Option> opts, Set<Argument> _) when descOptPresent(opts) -> {
                    final Description desc = getOpt(opts, Description.class);
                    final Optional<Start> start = getOpOpt(opts, Start.class);
                    final Optional<End> end = getOpOpt(opts, End.class);
                    yield new CreateTodoCommand(
                            desc.value(),
                            start.map(Start::value).map(LocalDateTime::parse).orElse(null),
                            end.map(End::value).map(LocalDateTime::parse).orElse(null)
                    );
                }
                case Spec(Todo _, List _, Set<Option> _, Set<Argument> _) -> new ListTodoCommand();
                case Spec(Todo _, Update _, Set<Option> opts, Set<Argument> _) when !opts.isEmpty() -> {
                    final Optional<Description> description = getOpOpt(opts, Description.class);
                    final Optional<Start> start = getOpOpt(opts, Start.class);
                    final Optional<End> end = getOpOpt(opts, End.class);
                    yield new UpdateTodoCommand(
                            Set.of(),
                            description.map(Description::value).orElse(null),
                            start.map(Start::value).map(LocalDateTime::parse).orElse(null),
                            end.map(End::value).map(LocalDateTime::parse).orElse(null)
                    );
                }
                case Spec(Todo _, Delete _, Set<Option> _, Set<Argument> args) when !args.isEmpty() ->
                        new DeleteTodoCommand(Set.of());
                case Spec(User _, Show _, Set<Option> _, Set<Argument> _) -> new UserShowCommand();
                case Spec(Calendar _, Show _, Set<Option> _, Set<Argument> _) -> new CalendarShowCommand();
                default -> throw new IllegalStateException("not supported");
            };
            return Result.ok(command);
        } catch (Exception exception) {
            return Result.err(exception);
        }
    }

    private static <T extends Option> Optional<T> getOpOpt(Set<Option> opts, Class<T> target) {
        return opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast);
    }

    private static <T extends Option> T getOpt(Set<Option> opts, Class<T> target) {
        return opts.stream()
                .filter(target::isInstance)
                .findFirst()
                .map(target::cast)
                .orElse(null);
    }

    private static boolean descOptPresent(Set<Option> opts) {
        return opts.stream().anyMatch(option -> option instanceof Description);
    }
}
