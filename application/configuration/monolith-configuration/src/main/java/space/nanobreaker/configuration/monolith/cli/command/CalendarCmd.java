package space.nanobreaker.configuration.monolith.cli.command;

public sealed interface CalendarCmd
        extends Command
        permits CalendarShowCmd {

    default String help() {
        return """
                  usage: calendar &lt;show&gt;
                  \s
                  commands:
                  show       display calendar information
                """;
    }

}
