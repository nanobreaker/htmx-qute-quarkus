package java.dev.thatwhichis.rest.adapter.services.parser;

import org.junit.jupiter.api.BeforeEach;
import java.dev.thatwhichis.rest.adapter.services.tokenizer.Tokenizer;

import java.time.Clock;
import java.time.ZoneId;

public abstract class ParserTestBase {

    protected Parser parser;
    protected Clock clock;

    protected Integer year;
    protected Integer month;
    protected Integer day;

    @BeforeEach
    void setUp() {
        final Tokenizer tokenizer = new Tokenizer();
        clock = Clock.systemUTC();
        parser = new Parser(clock, tokenizer);

        var now = clock.instant().atZone(ZoneId.of("UTC"));
        year = now.getYear();
        month = now.getMonth().getValue();
        day = now.getDayOfMonth();
    }
}
