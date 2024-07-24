package space.nanobreaker.configuration.monolith.cli.command;


public record ListTodoCmd(
) implements TodoCmd {

    public static String help() {
        return """
                  usage: todo list
                """;
    }
}