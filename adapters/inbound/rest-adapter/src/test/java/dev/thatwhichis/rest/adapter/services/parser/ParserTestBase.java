package dev.thatwhichis.rest.adapter.services.parser;

import dev.thatwhichis.rest.adapter.parsing.Parser;
import dev.thatwhichis.rest.adapter.parsing.token.Tokenizer;
import org.junit.jupiter.api.BeforeEach;

import java.time.Clock;
import java.time.ZoneId;

public abstract class ParserTestBase {

    protected Parser parser;

    protected Integer year;
    protected Integer month;
    protected Integer day;

    @BeforeEach
    void setUp() {
        final Tokenizer tokenizer = new Tokenizer();
        parser = new Parser(tokenizer);

        var now = Clock.systemUTC().instant().atZone(ZoneId.of("UTC"));
        year = now.getYear();
        month = now.getMonth().getValue();
        day = now.getDayOfMonth();
    }
}
