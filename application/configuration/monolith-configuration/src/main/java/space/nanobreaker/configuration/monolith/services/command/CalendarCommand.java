package space.nanobreaker.configuration.monolith.services.command;

public sealed interface CalendarCommand
        extends Command
        permits ShowCalendarCommand {

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