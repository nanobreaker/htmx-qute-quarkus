package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.qute.TemplateExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;

@TemplateExtension
public class TodoExtensions {

    private static final DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendPattern("dd LLL")
            .toFormatter();

    private static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .toFormatter();


    public static String formatId(
            final Integer id
    ) {
        return String.format("%03d", id);
    }

    public static String formatDate(
            final LocalDateTime dateTime
    ) {
        return dateTime.format(dateFormatter);
    }

    public static String formatDate(
            final Optional<LocalDateTime> dateTime
    ) {
        return dateTime.map(date -> date.format(dateFormatter))
                .orElseThrow();
    }

    public static String formatTime(
            final Optional<LocalDateTime> dateTime
    ) {
        return dateTime.map(date -> date.format(timeFormatter))
                .orElseThrow();
    }

    public static Boolean isTimePresent(
            final Optional<LocalDateTime> dateTime
    ) {
        return dateTime.map(date -> {
                    final int hour = date.getHour();
                    final int minute = date.getMinute();

                    return hour != 0 || minute != 0;
                })
                .orElse(false);
    }

}
