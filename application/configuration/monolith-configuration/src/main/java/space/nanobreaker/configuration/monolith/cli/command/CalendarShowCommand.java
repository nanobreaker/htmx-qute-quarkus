package space.nanobreaker.configuration.monolith.cli.command;

public record CalendarShowCommand() implements CalendarCommand {

    public String help() {
        return """
                  usage: calendar show
                """;
    }

}
