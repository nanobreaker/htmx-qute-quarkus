package space.nanobreaker.configuration.monolith.cli.command;

public sealed interface CalendarCommand
        extends CliCommand
        permits CalendarShowCommand {

    default String help() {
        return """
                  usage: calendar &lt;show&gt;
                  \s
                  commands:
                  show       display calendar information
                """;
    }

}
