package space.nanobreaker.configuration.monolith.extensions;

import io.quarkus.qute.TemplateExtension;
import space.nanobreaker.library.option.Option;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@TemplateExtension(namespace = "date")
public class DateTimeExtension {

    private static final DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendPattern("dd LLL")
            .toFormatter();

    private static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .toFormatter();

    public static String formatDate(
            final Option<ZonedDateTime> zonedDateTime,
            final ZoneId zoneId
    ) {
        return zonedDateTime
                .map(d -> d.withZoneSameLocal(zoneId))
                .map(d -> d.format(dateFormatter))
                .orElse("");
    }

    public static String formatTime(
            final Option<ZonedDateTime> zonedDateTime,
            final ZoneId zoneId
    ) {
        return zonedDateTime
                .map(d -> d.withZoneSameLocal(zoneId))
                .map(d -> d.format(timeFormatter))
                .orElse("");
    }

    public static Boolean isDateTimePresent(final Option<ZonedDateTime> zonedDateTime) {
        return zonedDateTime
                .map(d -> d.getHour() != 0 || d.getMinute() != 0)
                .orElse(false);
    }

    public static Boolean isDatePresent(final Option<ZonedDateTime> zonedDateTime) {
        return zonedDateTime.isSome();
    }
}
