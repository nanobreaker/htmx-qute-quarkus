package space.nanobreaker.configuration.monolith.services.command;

public record CalendarShowCmd() implements CalendarCmd {

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