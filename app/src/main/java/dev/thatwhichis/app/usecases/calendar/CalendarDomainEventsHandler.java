package dev.thatwhichis.app.usecases.calendar;

import dev.thatwhichis.core.domain.todo.TodoEvent;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalendarDomainEventsHandler {

    @ConsumeEvent(value = "todo.created")
    @WithTransaction
    public Uni<Void> handle(final TodoEvent.Created todoCreatedEvent) {
        // todo: create calendar item based on event
        return Uni.createFrom().voidItem();
    }

    @ConsumeEvent(value = "todo.deleted")
    @WithTransaction
    public Uni<Void> handle(final TodoEvent.Deleted todoDeletedEvent) {
        // todo: delete calendar items based on event
        return Uni.createFrom().voidItem();
    }

    @ConsumeEvent(value = "todo.deleted.all")
    @WithTransaction
    public Uni<Void> handle(final TodoEvent.DeletedAll todoDeletedAllEvent) {
        // todo: delete calendar items based on event
        return Uni.createFrom().voidItem();
    }

    @ConsumeEvent(value = "todo.updated")
    @WithTransaction
    public Uni<Void> handle(final TodoEvent.Updated todoUpdatedEvent) {
        // todo: update calendar items based on event
        return Uni.createFrom().voidItem();
    }
}
