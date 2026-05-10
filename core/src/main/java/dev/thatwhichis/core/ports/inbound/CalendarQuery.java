package dev.thatwhichis.core.ports.inbound;

import dev.thatwhichis.framework.cqrs.Query;

sealed public interface CalendarQuery extends Query {

    record Show() implements CalendarQuery {

    }
}
