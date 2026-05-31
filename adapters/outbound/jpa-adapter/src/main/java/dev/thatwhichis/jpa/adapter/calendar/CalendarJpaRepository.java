package dev.thatwhichis.jpa.adapter.calendar;

import dev.thatwhichis.core.domain.calendar.Calendar;
import dev.thatwhichis.core.ports.outbound.calendar.CalendarRepository;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.error.JpaError;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class CalendarJpaRepository implements CalendarRepository, PanacheRepositoryBase<CalendarJpaEntity, UUID> {

    @Override
    public Uni<Result<Calendar, Error>> save(Calendar calendar) {
        var jpaEntity = CalendarJpaEntity.from(calendar);

        return PanacheRepositoryBase.super
                .persistAndFlush(jpaEntity)
                .map(CalendarJpaEntity::into)
                .map(Result::<Calendar, Error>ok)
                .onFailure().recoverWithItem(throwable -> err(new JpaError.Uncategorized(throwable)));
    }

    @Override
    public Uni<Result<Calendar, Error>> get(UUID calendarId) {
        return this.findById(calendarId)
                .map(CalendarJpaEntity::into)
                .map(Result::<Calendar, Error>ok)
                .onFailure().recoverWithItem(throwable -> switch (throwable) {
                    case NullPointerException _ -> err(new JpaError.EntityNotFound());
                    default -> err(new JpaError.Uncategorized(throwable));
                });
    }

    @Override
    public Uni<Result<Option<Calendar>, Error>> find(UUID calendarId) {
        return this.findById(calendarId)
                .map(Option::some)
                .map(option -> option.map(CalendarJpaEntity::into))
                .map(Result::<Option<Calendar>, Error>ok)
                .onFailure().recoverWithItem(throwable -> err(new JpaError.Uncategorized(throwable)));
    }
}
