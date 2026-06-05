package dev.thatwhichis.core.ports.outbound.calendar;

import dev.thatwhichis.core.domain.calendar.Calendar;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface CalendarRepository {

    Uni<Result<Calendar, Error>> save(Calendar calendar);

    Uni<Result<Calendar, Error>> get(UUID calendarId);

    Uni<Result<Option<Calendar>, Error>> find(UUID calendarId);
}
