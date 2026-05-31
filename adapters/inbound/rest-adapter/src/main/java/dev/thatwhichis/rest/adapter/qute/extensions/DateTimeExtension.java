package dev.thatwhichis.rest.adapter.qute.extensions;

import dev.thatwhichis.library.option.Option;
import io.quarkus.qute.TemplateExtension;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@TemplateExtension(namespace = "date")
public class DateTimeExtension {

    private final static DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd LLL yyyy HH:mm")
            .withZone(ZoneOffset.UTC);

    public static String format(final Option<ZonedDateTime> zonedDateTime) {
        return zonedDateTime
                .map(d -> d.format(formatter))
                .orElse("");
    }

    public static String format(final Instant instant) {
        return formatter.format(instant);
    }
}
