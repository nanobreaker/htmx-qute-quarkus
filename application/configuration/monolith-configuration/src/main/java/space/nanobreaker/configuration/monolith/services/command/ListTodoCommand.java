package space.nanobreaker.configuration.monolith.services.command;


import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.result.Result;

import java.util.Set;

public record ListTodoCommand(
        Option<Set<Integer>> ids
) implements TodoCommand {

    public static Result<Command, Error> of(final Set<Integer> ids) {
        try {
            return Result.ok(new ListTodoCommand(Option.of(ids)));
        } catch (Exception e) {
            return Result.err(new CommandError.CreationFailed(e.getMessage()));
        }
    }

    public static Result<Command, Error> of() {
        try {
            return Result.ok(new ListTodoCommand(Option.none()));
        } catch (Exception e) {
            return Result.err(new CommandError.CreationFailed(e.getMessage()));
        }
    }

    public static String help() {
        return """
                   \s
                   usage
                   \s
                     todo list ["<arg>"]
                   \s
                   argument
                   \s
                     arg       string                       id of todo
                   \s
                   examples
                   \s
                     todo list
                     todo list "1" "2" "2"
                   \s
                """;
    }
}