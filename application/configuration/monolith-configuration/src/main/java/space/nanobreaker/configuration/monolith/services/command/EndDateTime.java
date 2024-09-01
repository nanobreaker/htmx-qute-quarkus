package space.nanobreaker.configuration.monolith.services.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public record EndDateTime(
        LocalDate date,
        LocalTime time
) {
    public static EndDateTime of(
            final LocalDate date,
            final LocalTime time
    ) {
        return new EndDateTime(date, time);
    }

    public static EndDateTime of(
            final LocalDate date
    ) {
        return new EndDateTime(date, null);
    }

    public static EndDateTime of(
            final LocalTime time
    ) {
        return new EndDateTime(null, time);
    }

    public LocalDateTime toDateTime() {
        if (Objects.isNull(date) && Objects.isNull(time))
            return null;

        if (Objects.isNull(date))
            return time.atDate(LocalDate.now());

        if (Objects.isNull(time))
            return date.atStartOfDay();

        return LocalDateTime.of(date, time);
    }
}