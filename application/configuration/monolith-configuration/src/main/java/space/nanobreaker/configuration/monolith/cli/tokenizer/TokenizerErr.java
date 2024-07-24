package space.nanobreaker.configuration.monolith.cli.tokenizer;

import space.nanobreaker.library.Error;

public sealed interface TokenizerErr extends Error {

    // @formatter:off
    record EmptyInput() implements TokenizerErr { }
    // @formatter:on

}