package space.nanobreaker.configuration.monolith.cli.command;

public record CalendarShowCmd() implements CalendarCmd {

    public static String help() {
        return """
                  usage: calendar show
                """;
    }

}