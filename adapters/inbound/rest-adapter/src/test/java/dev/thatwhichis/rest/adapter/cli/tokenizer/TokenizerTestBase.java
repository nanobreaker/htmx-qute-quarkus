package dev.thatwhichis.rest.adapter.cli.tokenizer;

import org.junit.jupiter.api.BeforeEach;

public abstract class TokenizerTestBase {

    protected Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
    }
}
