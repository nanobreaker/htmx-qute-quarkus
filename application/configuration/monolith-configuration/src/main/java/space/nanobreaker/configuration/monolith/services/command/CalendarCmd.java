package space.nanobreaker.configuration.monolith.services.command;

public sealed interface CalendarCmd
        extends Command
        permits CalendarShowCmd {

    static String help() {
        return """
                  \s
                  usage
                  \s
                    calendar <command>
                  \s
                  commands
                  \s
                    show    display calendar of the user
                  \s
                """;
    }

}