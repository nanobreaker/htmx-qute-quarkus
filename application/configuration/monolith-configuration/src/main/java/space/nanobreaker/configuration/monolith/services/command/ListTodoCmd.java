package space.nanobreaker.configuration.monolith.services.command;


public record ListTodoCmd(
) implements TodoCmd {

    public static String help() {
        return """
                  usage: todo list
                """;
    }
}