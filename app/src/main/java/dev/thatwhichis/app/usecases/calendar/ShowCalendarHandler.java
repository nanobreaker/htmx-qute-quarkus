package dev.thatwhichis.app.usecases.calendar;

import dev.thatwhichis.core.domain.calendar.Calendar;
import dev.thatwhichis.core.ports.inbound.calendar.CalendarQuery;
import dev.thatwhichis.core.ports.outbound.calendar.CalendarRepository;
import dev.thatwhichis.framework.cqrs.QueryHandler;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ShowCalendarHandler implements QueryHandler<CalendarQuery.Show, Calendar> {

    private final CalendarRepository repository;

    @Inject
    public ShowCalendarHandler(CalendarRepository repository) {
        this.repository = repository;
    }

    @Override
    @ConsumeEvent(value = "query.calendar.show")
    @WithSession
    public Uni<Result<Calendar, Error>> execute(CalendarQuery.Show query) {
        var userId = query.userId();

        return repository.get(userId);
    }
}
