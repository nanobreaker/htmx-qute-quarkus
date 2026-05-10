package dev.thatwhichis.core.domain.calendar;

import dev.thatwhichis.framework.ddd.Entity;

public class Calendar extends Entity<CalendarId> {

    protected Calendar(CalendarId calendarId) {
        super(calendarId);
    }
}
