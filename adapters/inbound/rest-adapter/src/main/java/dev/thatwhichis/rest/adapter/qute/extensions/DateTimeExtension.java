package dev.thatwhichis.rest.adapter.qute.extensions;

import dev.thatwhichis.library.option.Option;
import io.quarkus.qute.TemplateExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@TemplateExtension(namespace = "date")
public class DateTimeExtension {

    private final static DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd LLL yyyy HH:mm")
            .withZone(ZoneOffset.UTC);

    public static String format(
            final Option<Instant> instantOpt,
            final ZoneId zone
    ) {
        return instantOpt.map(instant -> formatter.withZone(zone).format(instant)).orElse("");
    }

    public static String format(
            final Instant instant,
            final ZoneId zone
    ) {
        return formatter.withZone(zone).format(instant);
    }
}
