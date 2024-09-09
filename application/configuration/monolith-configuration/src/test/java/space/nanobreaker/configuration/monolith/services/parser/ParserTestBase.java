package space.nanobreaker.configuration.monolith.services.parser;

import org.junit.jupiter.api.BeforeEach;
import space.nanobreaker.configuration.monolith.services.tokenizer.Tokenizer;

public abstract class ParserTestBase {

    protected Parser parser;

    @BeforeEach
    void setUp() {
        final Tokenizer tokenizer = new Tokenizer();
        parser = new Parser(tokenizer);
    }

}
