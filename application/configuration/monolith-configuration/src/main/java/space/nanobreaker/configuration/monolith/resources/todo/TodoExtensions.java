package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.qute.TemplateExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@TemplateExtension
public class TodoExtensions {

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("dd LLL")
            .toFormatter();

    public static String formatDate(
            final LocalDateTime dateTime
    ) {
        return dateTime.format(formatter);
    }

}
