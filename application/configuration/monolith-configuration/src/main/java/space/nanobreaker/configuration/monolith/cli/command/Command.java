package space.nanobreaker.configuration.monolith.cli.command;

public sealed interface Command permits
        TodoCmd,
        UserCmd,
        CalendarCmd {

    String help();

}

