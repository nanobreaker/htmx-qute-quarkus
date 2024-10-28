package space.nanobreaker.configuration.monolith.services.tokenizer;

import space.nanobreaker.library.error.Error;

public sealed interface TokenizerError extends Error {

    // @formatter:off
    record EmptyInput() implements TokenizerError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case EmptyInput _ -> "tokenizer error: empty command line";
        };
    }
}