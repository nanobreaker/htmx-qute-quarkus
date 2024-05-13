package space.nanobreaker.configuration.monolith.cli.command;

public sealed interface UserCommand
        extends CliCommand
        permits UserShowCommand {

    default String help() {
        return """
                  usage: user &lt;show&gt;
                  \s
                  commands:
                  show       display user information
                """;
    }

}
