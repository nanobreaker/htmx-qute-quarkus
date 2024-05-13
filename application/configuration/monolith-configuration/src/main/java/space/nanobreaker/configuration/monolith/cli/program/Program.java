package space.nanobreaker.configuration.monolith.cli.program;

public sealed interface Program permits
        TodoProgram,
        CalendarProgram,
        UserProgram {

}
