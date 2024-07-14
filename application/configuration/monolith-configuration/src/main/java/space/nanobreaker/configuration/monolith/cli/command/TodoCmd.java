package space.nanobreaker.configuration.monolith.cli.command;

public sealed interface TodoCmd extends Command permits
        DeleteTodoCmd,
        ListTodoCmd,
        CreateTodoCmd,
        UpdateTodoCmd {

    default String help() {
        return """
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
                """;
    }
}
