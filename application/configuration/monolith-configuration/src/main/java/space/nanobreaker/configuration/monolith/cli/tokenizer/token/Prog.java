package space.nanobreaker.configuration.monolith.cli.tokenizer.token;

public sealed interface Prog extends Token {
    record Todo() implements Prog {
    }

    record User() implements Prog {
    }

    record Calendar() implements Prog {
    }
}