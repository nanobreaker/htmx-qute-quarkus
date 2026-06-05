package dev.thatwhichis.jpa.adapter.calendar;

import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import dev.thatwhichis.core.ports.outbound.calendar.CalendarEntryRepository;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.error.JpaError;
import io.github.dcadea.jresult.Result;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class CalendarEntryJpaRepository implements CalendarEntryRepository, PanacheRepositoryBase<CalendarEntryJpaEntity, Integer> {

    @Override
    public Uni<Result<CalendarEntry, Error>> save(CalendarEntry entry) {
        var jpaEntity = CalendarEntryJpaEntity.from(entry);

        return this
                .persistAndFlush(jpaEntity)
                .map(CalendarEntryJpaEntity::into)
                .map(Result::<CalendarEntry, Error>ok)
                .onFailure().recoverWithItem(throwable -> err(new JpaError.Uncategorized(throwable)));
    }

    @Override
    public Uni<Result<CalendarEntry, Error>> get(Integer id) {
        return this
                .findById(id)
                .map(CalendarEntryJpaEntity::into)
                .map(Result::<CalendarEntry, Error>ok)
                .onFailure().recoverWithItem(throwable -> switch (throwable) {
                    case NullPointerException _ -> err(new JpaError.EntityNotFound());
                    default -> err(new JpaError.Uncategorized(throwable));
                });
    }

    @Override
    public Uni<Result<Void, Error>> update(final CalendarEntry entry) {
        var jpaEntity = CalendarEntryJpaEntity.from(entry);

        return this
                .findById(jpaEntity.getId())
                .flatMap(entity -> {
                    if (entity == null) {
                        return Uni.createFrom().item(Result.<Void, Error>err(new JpaError.EntityNotFound()));
                    }

                    entity.setStart(jpaEntity.getStart());
                    entity.setEnd(jpaEntity.getEnd());

                    return this.flush().replaceWith(Result.<Void, Error>empty());
                })
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Void, Error>> delete(Integer id) {
        return this
                .deleteById(id)
                .map(result -> {
                    // todo: java 25 preview supports switch over primitive types
                    if (result) {
                        return Result.<Void, Error>empty();
                    } else {
                        return Result.<Void, Error>err(new JpaError.DeleteNotFound());
                    }
                })
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Void, Error>> deleteAll(UUID calendarId) {
        return this
                .find("calendarId", calendarId)
                .list()
                .flatMap(entries -> {
                    var deletes = entries
                            .stream()
                            .map(this::delete)
                            .toList();

                    if (deletes.isEmpty()) {
                        return Uni.createFrom().item(Result.<Void, Error>empty());
                    }

                    return Uni.join()
                            .all(deletes)
                            .andFailFast()
                            .chain(this::flush)
                            .replaceWith(Result.<Void, Error>empty());
                })
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }
}
