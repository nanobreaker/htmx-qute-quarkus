package space.nanobreaker.configuration.monolith.services.command;

public record ShowCalendarCommand() implements CalendarCommand {

    public static String help() {
        return """
                   \s
                   usage
                   \s
                     calendar show
                   \s
                   description
                   \s
                     display calendar of the user
                   \s
                """;
    }
}