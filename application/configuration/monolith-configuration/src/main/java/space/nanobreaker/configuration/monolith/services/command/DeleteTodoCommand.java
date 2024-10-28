package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.result.Result;

import java.util.Set;

public record DeleteTodoCommand(
        Set<Integer> ids
) implements TodoCommand {

    public static Result<Command, Error> of(final Set<Integer> ids) {
        try {
            return Result.ok(new DeleteTodoCommand(ids));
        } catch (Exception e) {
            return Result.err(new CommandError.CreationFailed(e.getMessage()));
        }
    }

    public static Result<Command, Error> of() {
        try {
            return Result.ok(new DeleteTodoCommand(Set.of()));
        } catch (Exception e) {
            return Result.err(new CommandError.CreationFailed(e.getMessage()));
        }
    }

    public static String help() {
        return """
                   \s
                   usage
                   \s
                     todo delete "<arg(s)>"
                   \s
                   argument
                   \s
                     arg       string                       id of todo
                   \s
                   examples
                   \s
                     todo delete "1" "2" "3"
                   \s
                """;
    }
}
