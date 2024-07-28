package space.nanobreaker.configuration.monolith.services.command;

public sealed interface Command permits
        TodoCmd,
        UserCmd,
        CalendarCmd {

    static String help() {
        return """
                  usage: &lt;program&gt; &lt;command&gt; &lt;args&gt; [options]
                  \s
                  programs:
                  todo          manage todos
                  calendar      manage calendar
                  user          manage user
                """;
    }

}