package space.nanobreaker.configuration.monolith.cli.program;

public enum OPTION {

    DESCRIPTION("-d", "description"),
    START_DATE_TIME("-s", "start date time"),
    END_DATE_TIME("-e", "end date time"),
    ;

    final String key;
    final String help;

    OPTION(final String key,
           final String help) {
        this.key = key;
        this.help = help;
    }
}
