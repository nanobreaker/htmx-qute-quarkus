package space.nanobreaker.configuration.monolith.services.command;


public record ListTodoCmd(
) implements TodoCmd {

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