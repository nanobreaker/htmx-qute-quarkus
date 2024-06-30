package space.nanobreaker.configuration.monolith.cli.parser.token;

public sealed interface Program extends Token {
    record Todo() implements Program {
    }

    record User() implements Program {
    }

    record Calendar() implements Program {
    }
}