package space.nanobreaker.configuration.monolith.services.command;

public sealed interface Command permits
        TodoCmd,
        UserCmd,
        CalendarCmd {

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