package space.nanobreaker.configuration.monolith.cli.tokenizer;

import space.nanobreaker.configuration.monolith.extension.Error;

public sealed interface TokenizerErr extends Error {

    // @formatter:off
    record EmptyInput() implements TokenizerErr { }
    // @formatter:on

}