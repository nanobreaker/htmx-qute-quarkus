package dev.thatwhichis.rest.adapter.cli.tokenizer;

import dev.thatwhichis.library.error.Error;

public sealed interface TokenizerError extends Error {

    // @formatter:off
    record EmptyInput() implements TokenizerError { }
    record UnknownKeyword(String keyword) implements TokenizerError { }
    record Unreachable() implements TokenizerError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case EmptyInput _ -> "tokenizer error: empty command line";
            case Unreachable _ -> "tokenizer error: unreachable branch reached";
            case UnknownKeyword _ -> "tokenizer error: unknown keyword";
        };
    }
}