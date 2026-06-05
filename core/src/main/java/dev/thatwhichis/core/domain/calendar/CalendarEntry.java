package dev.thatwhichis.core.domain.calendar;

import dev.thatwhichis.library.option.Option;

import java.time.Instant;
import java.util.UUID;

public record CalendarEntry(
        Integer id,
        UUID calendarId,
        Option<Instant> start,
        Option<Instant> end
) {

}
