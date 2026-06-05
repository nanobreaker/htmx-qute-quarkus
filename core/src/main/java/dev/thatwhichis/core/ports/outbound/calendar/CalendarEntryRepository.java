package dev.thatwhichis.core.ports.outbound.calendar;

import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface CalendarEntryRepository {

    Uni<Result<CalendarEntry, Error>> save(CalendarEntry entry);

    Uni<Result<CalendarEntry, Error>> get(Integer id);

    Uni<Result<Void, Error>> update(CalendarEntry entry);

    Uni<Result<Void, Error>> delete(Integer id);

    Uni<Result<Void, Error>> deleteAll(UUID calendarId);
}
