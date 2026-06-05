package dev.thatwhichis.jpa.adapter.calendar;

import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

import static dev.thatwhichis.library.option.Option.some;

@Entity
public class CalendarEntryJpaEntity {

    @Id
    private Integer id;

    @Column(name = "calendar_id", nullable = false)
    private UUID calendarId;

    @Column(name = "start_timestamp")
    private Instant start;

    @Column(name = "end_timestamp")
    private Instant end;

    public CalendarEntryJpaEntity(
            final Integer id,
            final UUID calendarId,
            final Instant start,
            final Instant end
    ) {
        this.id = id;
        this.calendarId = calendarId;
        this.start = start;
        this.end = end;
    }

    public CalendarEntryJpaEntity() {

    }

    public Integer getId() {
        return id;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public CalendarEntry into() {
        return new CalendarEntry(
                id,
                calendarId,
                some(start),
                some(end)
        );
    }

    public static CalendarEntryJpaEntity from(final CalendarEntry entry) {
        return new CalendarEntryJpaEntity(
                entry.id(),
                entry.calendarId(),
                entry.start().orNull(),
                entry.end().orNull()
        );
    }
}
