package space.nanobreaker.core.domain.v1.calendar;

import space.nanobreaker.ddd.AggregateRoot;

public class Calendar extends AggregateRoot<CalendarId> {

    protected Calendar(CalendarId calendarId) {
        super(calendarId);
    }
}
