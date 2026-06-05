package dev.thatwhichis.app.usecases.calendar;

import dev.thatwhichis.core.domain.calendar.Calendar;
import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import dev.thatwhichis.core.domain.todo.TodoEvent;
import dev.thatwhichis.core.domain.user.UserEvent;
import dev.thatwhichis.core.ports.outbound.calendar.CalendarEntryRepository;
import dev.thatwhichis.core.ports.outbound.calendar.CalendarRepository;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;

import static io.github.dcadea.jresult.Result.empty;
import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class CalendarDomainEventsHandler {

    private final CalendarRepository repository;
    private final CalendarEntryRepository entryRepository;

    @Inject
    public CalendarDomainEventsHandler(
            final CalendarRepository repository,
            final CalendarEntryRepository entryRepository
    ) {
        this.repository = repository;
        this.entryRepository = entryRepository;
    }

    @ConsumeEvent(value = "user.created")
    @WithTransaction
    public Uni<Result<Void, Error>> handle(final UserEvent.Created event) {
        var userId = event.user().getId();
        var calendar = new Calendar.Builder(userId)
                .withNoEntries()
                .build();

        return repository.save(calendar)
                .map(result -> switch (result) {
                    case Ok(_) -> {
                        Log.infof("User calendar created [id: %s]", userId);

                        yield empty();
                    }
                    case Err(var e) -> {
                        Log.errorf("User calendar creation failed [id: %s, error: %s]", userId, e.describe());

                        yield err(e);
                    }
                });
    }

    @ConsumeEvent(value = "todo.created")
    @WithTransaction
    public Uni<Result<Void, Error>> handle(final TodoEvent.Created event) {
        var userId = event.todo().getId().userId();
        var todoId = event.todo().getId().id();
        var start = event.todo().getStart();
        var end = event.todo().getEnd();
        var entry = new CalendarEntry(todoId, userId, start, end);

        return entryRepository
                .save(entry)
                .map(result -> switch (result) {
                    case Ok(_) -> Result.empty();
                    case Err(var e) -> err(e);
                });
    }

    @ConsumeEvent(value = "todo.deleted")
    @WithTransaction
    public Uni<Result<Void, Error>> handle(final TodoEvent.Deleted event) {
        var todoId = event.todoId().id();

        return entryRepository
                .delete(todoId);
    }

    @ConsumeEvent(value = "todo.deleted.all")
    @WithTransaction
    public Uni<Result<Void, Error>> handle(final TodoEvent.DeletedAll event) {
        var userId = event.userId();

        return entryRepository
                .deleteAll(userId);
    }

    @ConsumeEvent(value = "todo.updated")
    @WithTransaction
    public Uni<Result<Void, Error>> handle(final TodoEvent.Updated event) {
        var todoId = event.todo().getId().id();
        var userId = event.todo().getId().userId();
        var payload = event.payload();

        Option<Instant> start = event.todo().getStart();
        if (payload.start().isSome()) {
            start = payload.start();
        }

        Option<Instant> end = payload.end();
        if (payload.end().isSome()) {
            end = payload.end();
        }

        var updated = new CalendarEntry(todoId, userId, start, end);

        return entryRepository
                .update(updated);
    }
}
