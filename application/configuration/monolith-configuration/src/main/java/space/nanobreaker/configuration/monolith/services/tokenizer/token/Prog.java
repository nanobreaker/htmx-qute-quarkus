package space.nanobreaker.configuration.monolith.services.tokenizer.token;

public sealed interface Prog extends Token {
    record Todo() implements Prog {
    }

    record User() implements Prog {
    }

    record Calendar() implements Prog {
    }
}