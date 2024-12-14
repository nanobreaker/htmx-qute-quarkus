package space.nanobreaker.core.usecases.v1.calendar;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.todo.TodoEvent;

@ApplicationScoped
public class CalendarDomainEventsHandler {

    @ConsumeEvent(value = "todo.created")
    @WithSpan("handleTodoCreatedEvent")
    @WithTransaction
    public Uni<Void> handle(final TodoEvent.Created todoCreatedEvent) {
        // todo: create calendar item based on event
        return Uni.createFrom().voidItem();
    }

    @ConsumeEvent(value = "todo.deleted")
    @WithSpan("handleTodoCreatedEvent")
    @WithTransaction
    public Uni<Void> handle(final TodoEvent.Deleted todoDeletedEvent) {
        // todo: delete calendar items based on event
        return Uni.createFrom().voidItem();
    }

    @ConsumeEvent(value = "todo.updated")
    @WithSpan("handleTodoCreatedEvent")
    @WithTransaction
    public Uni<Void> handle(final TodoEvent.Updated todoUpdatedEvent) {
        // todo: update calendar items based on event
        return Uni.createFrom().voidItem();
    }
}
