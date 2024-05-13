package space.nanobreaker.configuration.monolith.cli.command;

public sealed interface CliCommand permits
        TodoCommand,
        UserCommand,
        CalendarCommand {

    String help();

}

