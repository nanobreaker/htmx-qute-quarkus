package java.dev.thatwhichis.rest.adapter.services.tokenizer;

import org.junit.jupiter.api.BeforeEach;

public abstract class TokenizerTestBase {

    protected Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
    }

}
