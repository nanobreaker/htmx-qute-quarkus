package dev.thatwhichis.rest.adapter.cli;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommandDescriber {

    private static final String help = """
            USAGE
                <program> <command> "<args>" [options]
            \s
            PROGRAMS
                help
                todo
                user
                calendar
                logout
            """;

    private static final String logout = """
            USAGE
                logout
            """;

    private static final String todo_help = """
            USAGE 
                todo <command> "<args>" [options]
            \s
            COMMANDS
                create
                list
                update
                delete
            """;

    private static final String todo_create_help = """
            USAGE
                todo create "<arg>" [-d"<description>"] [-s"<start>"] [-e"<end>"]
            \s
            ARGUMENT
                arg       string, alphanumeric        title
            \s
            OPTIONS 
                -d        string, alphanumeric        description
                -s        string, dd/mm/yy hh:mm      start date time
                -e        string, dd/mm/yy hh:mm      end date time
            \s
            DESCRIPTION
                create todo with a given title and optional description, start and end date time
            \s
            EXAMPLES 
                todo create "country trip" -d"prepare car" -s"21 09:00" -e"22 18:00"
                todo create "vacation" -d"barcelona again?" -s"04/12" -e"22/12"
            """;

    private static final String todo_list_help = """
            USAGE
                todo list ["<arg>"]
            \s
            ARGUMENT 
                arg       string, numeric             id
            \s
            DESCRIPTION 
                list all todos or those matching the given id
            \s
            EXAMPLES 
                todo list
                todo list "1" "2" "2"
            \s
            """;

    private static final String todo_update_help = """
            USAGE 
                todo update ["<arg(s)>"] [-f"<title>"] [-t"<title>"] [-d"<description>"] [-s"<start>"] [-e"<end>"]
            \s
            ARGUMENT 
                arg       string                      id
            \s
            OPTIONS 
                -f        string, alphanumeric        filter by todo title
                -t        string, alphanumeric        title
                -d        string, alphanumeric        description
                -s        string, dd/mm/yy hh:mm      start date time
                -e        string, dd/mm/yy hh:mm      end date time
            \s
            DESCRIPTION 
                update todo(s) by given id or optional filter with new title, description, start and end date time 
            \s
            EXAMPLES 
                todo update -f"trip to barcelona" -d"check in day before" -s"21 09:00" -e"22 18:00"
                todo update -f"doggy" -d"buy new bottle"
                todo update "0" -d"buy new bottle"
            \s
            """;

    private static final String todo_delete_help = """
            USAGE 
                todo delete "<arg(s)>"
            \s
            ARGUMENT 
                arg       string                      id
            \s
            EXAMPLES 
                todo delete "1" "2" "3"
            """;

    private static final String calendar_help = """
            USAGE
                 calendar <command>
            \s
            COMMANDS 
                 show
            \s
            """;

    private static final String calendar_show_help = """
            USAGE 
                calendar show
            \s
            DESCRIPTION
                display calendar of the user
            """;

    private static final String user_help = """
            USAGE 
                user <command>
            \s
            COMMANDS 
                show
            """;

    private static final String user_show_help = """
            USAGE 
                user show
            \s
            DESCRIPTION 
                display user information and statistics
            """;

    public String describe(Command command) {
        return switch (command) {
            //@formatter:off
            case Command.Help           _ -> help;
            case Command.Logout         _ -> logout;
            case Command.Todo.Help      _ -> todo_help;
            case Command.Todo.Create    _ -> todo_create_help;
            case Command.Todo.List      _ -> todo_list_help;
            case Command.Todo.Update    _ -> todo_update_help;
            case Command.Todo.Delete    _ -> todo_delete_help;
            case Command.Calendar.Help  _ -> calendar_help;
            case Command.Calendar.Show  _ -> calendar_show_help;
            case Command.User.Help      _ -> user_help;
            case Command.User.Show      _ -> user_show_help;
            //@formatter:on
        };
    }
}
