package dev.thatwhichis.app.usecases.calendar;

import dev.thatwhichis.core.ports.inbound.calendar.CalendarQuery;
import dev.thatwhichis.framework.cqrs.QueryHandler;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShowCalendarHandler implements QueryHandler<CalendarQuery.Show, Void> {

    @Override
    public Uni<Result<Void, Error>> execute(CalendarQuery.Show query) {
        return Uni.createFrom().item(Result.empty());
    }
}
