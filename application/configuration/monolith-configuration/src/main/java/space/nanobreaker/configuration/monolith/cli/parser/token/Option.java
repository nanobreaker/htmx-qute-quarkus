package space.nanobreaker.configuration.monolith.cli.parser.token;

public sealed interface Option extends Token {
    record Title(String value) implements Option {
    }

    record Description(String value) implements Option {
    }

    record Start(String value) implements Option {
    }

    record End(String value) implements Option {
    }
}