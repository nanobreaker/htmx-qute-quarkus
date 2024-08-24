package space.nanobreaker.configuration.monolith.services.command;

public record UserShowCmd() implements UserCmd {

    public static String help() {
        return """
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
    }

}