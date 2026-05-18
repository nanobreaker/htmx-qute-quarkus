package dev.thatwhichis.core.ports.inbound.calendar;

import dev.thatwhichis.framework.cqrs.Query;

sealed public interface CalendarQuery extends Query {

    record Show() implements CalendarQuery {

    }
}
