package dev.thatwhichis.core.ports.inbound.calendar;

import dev.thatwhichis.framework.cqrs.Query;

import java.util.UUID;

sealed public interface CalendarQuery extends Query {

    //@formatter:off
    record Show(UUID userId) implements CalendarQuery { }
    //@formatter:on
}
