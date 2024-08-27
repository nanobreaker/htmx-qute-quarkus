package space.nanobreaker.configuration.monolith.services.command;

public sealed interface TodoCmd extends Command permits
        DeleteTodoCmd,
        ListTodoCmd,
        CreateTodoCmd,
        UpdateTodoCmd {

    static String help() {
        return """
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
    }
}