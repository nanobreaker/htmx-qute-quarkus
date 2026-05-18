package dev.thatwhichis.rest.adapter.qute.extensions;

import dev.thatwhichis.library.option.Option;
import io.quarkus.qute.TemplateExtension;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@TemplateExtension(namespace = "date")
public class DateTimeExtension {

    public static String format(final Option<ZonedDateTime> zonedDateTime) {
        return zonedDateTime
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd LLL yyyy HH:mm")))
                .orElse("");
    }
}
