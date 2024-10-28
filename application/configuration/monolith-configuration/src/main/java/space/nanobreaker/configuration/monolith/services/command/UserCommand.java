package space.nanobreaker.configuration.monolith.services.command;

public sealed interface UserCommand
        extends Command
        permits ShowUserCommand {

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