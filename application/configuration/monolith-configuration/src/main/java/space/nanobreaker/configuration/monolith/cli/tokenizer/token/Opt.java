package space.nanobreaker.configuration.monolith.cli.tokenizer.token;

public sealed interface Opt extends Token {
    record Title(String value) implements Opt {
    }

    record Description(String value) implements Opt {
    }

    record Start(String value) implements Opt {
    }

    record End(String value) implements Opt {
    }
}