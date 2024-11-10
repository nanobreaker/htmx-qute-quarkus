package space.nanobreaker.configuration.monolith.extensions;

import io.quarkus.qute.TemplateExtension;
import space.nanobreaker.library.option.Option;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@SuppressWarnings("ALL")
@TemplateExtension
public class TodoExtensions {

    enum DateTimeState {
        DATETIME,
        DATE,
        TIME,
        NONE
    }

    private static final DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendPattern("dd LLL")
            .toFormatter();

    private static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .toFormatter();

    public static String formatId(final Integer id) {
        return String.format("%03d", id);
    }

    public static String formatDate(final Option<ZonedDateTime> dateTime) {
        return dateTime.map(date -> date.format(dateFormatter))
                .orElseGet(() -> "");
    }

    public static String formatTime(final Option<ZonedDateTime> dateTime) {
        return dateTime.map(date -> date.format(timeFormatter))
                .orElseGet(() -> "");
    }

    public static Boolean isTimePresent(final Option<ZonedDateTime> dateTime) {
        return dateTime
                .map(date -> {
                    int hour = date.getHour();
                    int minute = date.getMinute();

                    return hour != 0 || minute != 0;
                })
                .orElse(false);
    }
}
