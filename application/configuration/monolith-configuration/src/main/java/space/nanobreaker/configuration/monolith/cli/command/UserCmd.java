package space.nanobreaker.configuration.monolith.cli.command;

public sealed interface UserCmd
        extends Command
        permits UserShowCmd {

    static String help() {
        return """
                  usage: user &lt;show&gt;
                  \s
                  commands:
                  show       display user information
                """;
    }

}