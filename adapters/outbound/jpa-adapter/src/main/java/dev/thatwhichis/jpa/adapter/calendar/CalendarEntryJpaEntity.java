package dev.thatwhichis.jpa.adapter.calendar;

import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

import static dev.thatwhichis.library.option.Option.some;

@Entity
public class CalendarEntryJpaEntity {

    @Id
    private Integer id;

    private Instant starts;
    private Instant ends;

    public CalendarEntryJpaEntity(Integer id, Instant starts, Instant ends) {
        this.id = id;
        this.starts = starts;
        this.ends = ends;
    }

    public CalendarEntryJpaEntity() {

    }

    public CalendarEntry into() {
        return new CalendarEntry(
                id,
                some(starts),
                some(ends)
        );
    }

    public static CalendarEntryJpaEntity from(CalendarEntry entry) {
        return new CalendarEntryJpaEntity(
                entry.id(),
                entry.start().orNull(),
                entry.end().orNull()
        );
    }
}
