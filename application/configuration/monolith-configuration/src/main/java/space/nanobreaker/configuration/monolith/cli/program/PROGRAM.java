package space.nanobreaker.configuration.monolith.cli.program;

import java.util.EnumSet;

public enum PROGRAM {

    TODO("""
              usage: todo &lt;subcommand&gt; [options]
              \s
              commands:
              create  "&lt;description&gt;"     create new todo
              list    &lt;id ids&gt;            list all todos
              update  &lt;id ids&gt;            update todo(s) by id(s)
              delete  &lt;id ids&gt;            delete todo(s) by id(s)

              options:
              -h --help                   more information
              -d --description string     description
              -s --start string           start date time
              -e --end string             end date time
              -p --priority string        priority
            """,
            EnumSet.of(
                    COMMAND.CREATE,
                    COMMAND.LIST,
                    COMMAND.DELETE,
                    COMMAND.UPDATE
            )
    ),
    CALENDAR("""
              usage: calendar &lt;command&gt; [options]
              \s
              commands:
              show          list calendar

              options:
              -h --help               more information
            """,
            EnumSet.of(
                    COMMAND.SHOW
            )
    ),
    USER("""
              usage: user &lt;command&gt;
              \s
              commands:
              show          get user information
            """,
            EnumSet.of(
                    COMMAND.SHOW
            )
    );

    final String help;
    final EnumSet<COMMAND> commands;

    PROGRAM(final String help,
            final EnumSet<COMMAND> commands) {
        this.help = help;
        this.commands = commands;
    }

    public EnumSet<COMMAND> getCommands() {
        return commands;
    }

    public String getHelp() {
        return help;
    }
}
