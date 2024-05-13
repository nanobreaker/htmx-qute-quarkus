package space.nanobreaker.configuration.monolith.cli.command;


public record ListTodoCommand(
) implements TodoCommand {

    public String help() {
        return """
                  usage: todo list
                """;
    }
}
