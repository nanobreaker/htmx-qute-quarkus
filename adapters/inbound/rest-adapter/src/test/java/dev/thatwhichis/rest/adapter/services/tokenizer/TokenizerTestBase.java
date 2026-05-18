package dev.thatwhichis.rest.adapter.services.tokenizer;

import dev.thatwhichis.rest.adapter.parsing.token.Tokenizer;
import org.junit.jupiter.api.BeforeEach;

public abstract class TokenizerTestBase {

    protected Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
    }
}
