package dev.thatwhichis.core.domain.calendar;

import dev.thatwhichis.library.option.Option;

import java.time.Instant;

public record CalendarEntry(
        Integer id,
        Option<Instant> start,
        Option<Instant> end
) {

}
