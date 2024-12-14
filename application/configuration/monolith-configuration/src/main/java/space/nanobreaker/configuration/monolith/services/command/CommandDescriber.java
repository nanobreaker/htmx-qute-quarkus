package space.nanobreaker.configuration.monolith.services.command;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommandDescriber {

    private static final String help = """
              \s
              usage
              \s
                <program> <command> "<args>" [options]
              \s
              programs
              \s
                todo          manage todos
                calendar      manage calendar
                user          manage user
              \s
            """;
    private static final String todo_help = """
              \s
              usage
              \s
                todo <command> "<args>" [options]
              \s
              commands
              \s
                create      create todo
                list        list todos
                update      update todo(s) by title(s)/id(s)
                delete      delete todo(s) by id(s)
              \s
            """;
    private static final String todo_create_help = """
               \s
               usage
               \s
                 todo create "<arg>" [-d"<description>"] [-s"<start>"] [-e"<end>"]
               \s
               argument
               \s
                 arg       string                       title of todo
               \s
               options
               \s
                 -d        string                       description of todo
                 -s        template dd/mm/yy hh:mm      start date time
                 -e        template dd/mm/yy hh:mm      end date time
               \s
               examples
               \s
                 todo create "country trip" -d"prepare car" -s"21 09:00" -e"22 18:00"
                 todo create "vacation" -d"barcelona again?" -s"04/12" -e"22/12"
               \s
            """;
    private static final String todo_list_help = """
               \s
               usage
               \s
                 todo list ["<arg>"]
               \s
               argument
               \s
                 arg       string                       id of todo
               \s
               examples
               \s
                 todo list
                 todo list "1" "2" "2"
               \s
            """;
    private static final String todo_update_help = """
               \s
               usage
               \s
                 todo update "<arg(s)>" [-t"<title>"] [-d"<description>"] [-s"<start>"] [-e"<end>"]
               \s
               argument
               \s
                 arg       string                       id of todo
               \s
               options
               \s
                 -f        string                       filter by todo title
                 -t        string                       title of todo
                 -d        string                       description of todo
                 -s        template dd/mm/yy hh:mm      start date time
                 -e        template dd/mm/yy hh:mm      end date time
               \s
               examples
               \s
                 todo update -f"trip to barcelona" -d"check in day before" -s"21 09:00" -e"22 18:00"
                 todo update -f"doggy" -d"buy new bottle"
                 todo update "0" -d"buy new bottle"
               \s
            """;
    private static final String todo_delete_help = """
               \s
               usage
               \s
                 todo delete "<arg(s)>"
               \s
               argument
               \s
                 arg       string                       id of todo
               \s
               examples
               \s
                 todo delete "1" "2" "3"
               \s
            """;
    private static final String calendar_help = """
              \s
              usage
              \s
                calendar <command>
              \s
              commands
              \s
                show    display calendar of the user
              \s
            """;
    private static final String calendar_show_help = """
               \s
               usage
               \s
                 calendar show
               \s
               description
               \s
                 display calendar of the user
               \s
            """;
    private static final String user_help = """
              \s
              usage
              \s
                user <command>
              \s
              commands
              \s
                show    display user information
              \s
            """;
    private static final String user_show_help = """
               \s
               usage
               \s
                 user show
               \s
               description
               \s
                 display user information
               \s
            """;

    public String describe(Command command) {
        return switch (command) {
            case Command.Help _ -> help;
            case Command.Todo.Help _ -> todo_help;
            case Command.Todo.Create _ -> todo_create_help;
            case Command.Todo.List _ -> todo_list_help;
            case Command.Todo.Update _ -> todo_update_help;
            case Command.Todo.Delete _ -> todo_delete_help;
            case Command.Calendar.Help _ -> calendar_help;
            case Command.Calendar.Show _ -> calendar_show_help;
            case Command.User.Help _ -> user_help;
            case Command.User.Show _ -> user_show_help;
        };
    }
}
