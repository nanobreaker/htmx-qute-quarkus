package space.nanobreaker.configuration.monolith.services.command;


import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.Set;

public record ListTodoCmd(Set<Integer> ids) implements TodoCmd {

    public static Result<Command, Error> of(final Set<Integer> ids) {
        try {
            return Result.ok(new ListTodoCmd(ids));
        } catch (Exception e) {
            return Result.err(new CmdErr.CreationFailed(e.getMessage()));
        }
    }

    public static Result<Command, Error> of() {
        try {
            return Result.ok(new ListTodoCmd(Set.of()));
        } catch (Exception e) {
            return Result.err(new CmdErr.CreationFailed(e.getMessage()));
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