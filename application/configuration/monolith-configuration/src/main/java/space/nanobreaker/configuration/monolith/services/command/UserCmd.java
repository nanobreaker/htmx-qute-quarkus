package space.nanobreaker.configuration.monolith.services.command;

public sealed interface UserCmd
        extends Command
        permits UserShowCmd {

    static String help() {
        return """
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
    }

}