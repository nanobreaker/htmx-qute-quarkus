package space.nanobreaker.configuration.monolith.services.command;

public sealed interface Command permits
        TodoCommand,
        UserCommand,
        CalendarCommand {

    static String help() {
        return """
                  \s
                  usage
                  \s
                    <program> <command> "<args>" [options]
                  \s
                  programs
                  \s
                    todo          manage todos
                    calendar      manage calendar
                    user          manage user
                  \s
                """;
    }
}