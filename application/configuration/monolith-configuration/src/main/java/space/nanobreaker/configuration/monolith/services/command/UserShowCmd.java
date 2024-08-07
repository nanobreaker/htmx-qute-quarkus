package space.nanobreaker.configuration.monolith.services.command;

public record UserShowCmd() implements UserCmd {

    public static String help() {
        return """
                  usage: user &lt;show&gt;
                  \s
                  commands:
                  show       display user information
                """;
    }

}