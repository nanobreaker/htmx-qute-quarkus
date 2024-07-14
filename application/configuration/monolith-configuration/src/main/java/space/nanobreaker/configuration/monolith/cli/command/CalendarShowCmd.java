package space.nanobreaker.configuration.monolith.cli.command;

public record CalendarShowCmd() implements CalendarCmd {

    public String help() {
        return """
                  usage: calendar show
                """;
    }

}
