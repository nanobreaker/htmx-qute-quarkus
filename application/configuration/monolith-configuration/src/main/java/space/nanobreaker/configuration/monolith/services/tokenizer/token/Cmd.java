package space.nanobreaker.configuration.monolith.services.tokenizer.token;

public sealed interface Cmd extends Token {
    record Create() implements Cmd {
    }

    record List() implements Cmd {
    }

    record Update() implements Cmd {
    }

    record Delete() implements Cmd {
    }

    record Show() implements Cmd {
    }
}
