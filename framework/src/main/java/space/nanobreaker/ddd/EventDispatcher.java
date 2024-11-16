package space.nanobreaker.ddd;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.library.error.Error;

import java.util.SequencedCollection;
import java.util.function.Supplier;

@ApplicationScoped
public class EventDispatcher {

    private final EventBus bus;

    public EventDispatcher(EventBus bus) {
        this.bus = bus;
    }

    public <R> Uni<Result<R, Error>> on(
            final Supplier<Uni<Result<R, Error>>> operation,
            final AggregateRoot<?> aggregateRoot
    ) {
        final Uni<Result<R, Error>> resultUni = operation.get();

        return resultUni.map(result -> {
            switch (result) {
                case Ok(R value) -> {
                    final SequencedCollection<Event> events = aggregateRoot.getDomainEvents();
                    for (Event event : events) {
                        // todo: uncomment when consumers are implemented
                        // bus.publish(event.key(), event);
                    }
                    return Result.ok(value);
                }
                case Err(Error error) -> {
                    return Result.err(error);
                }
            }
        });
    }

    public <R> Uni<Result<R, Error>> on(
            final Supplier<Uni<Result<R, Error>>> operation,
            final Event event
    ) {
        final Uni<Result<R, Error>> resultUni = operation.get();

        return resultUni.map(result -> {
            switch (result) {
                case Ok(R value) -> {
                    // todo: uncomment when consumers are implemented
                    // bus.publish(event.key(), event);
                    return Result.ok(value);
                }
                case Err(Error error) -> {
                    return Result.err(error);
                }
            }
        });
    }
}
