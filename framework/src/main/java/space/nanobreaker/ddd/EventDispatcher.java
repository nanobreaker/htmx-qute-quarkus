package space.nanobreaker.ddd;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.library.error.Error;

import java.util.Collection;
import java.util.function.Supplier;

@ApplicationScoped
public class EventDispatcher {

    private final EventBus eventBus;

    public EventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @WithSpan
    public <R> Uni<Result<R, Error>> on(
            final Supplier<Uni<Result<R, Error>>> operation,
            final DomainEvent event
    ) {
        final Uni<Result<R, Error>> resultUni = operation.get();

        return resultUni.map(result -> switch (result) {
            case Ok(R value) -> {
                eventBus.publish(event.key(), event);

                yield Result.ok(value);
            }
            case Err(Error error) -> {
                yield Result.err(error);
            }
        });
    }

    @WithSpan
    public <R> Uni<Result<R, Error>> on(
            final Supplier<Uni<Result<R, Error>>> operation,
            final Collection<DomainEvent> events
    ) {
        final Uni<Result<R, Error>> resultUni = operation.get();

        return resultUni.map(result -> switch (result) {
            case Ok(R value) -> {
                events.forEach(e -> eventBus.publish(e.key(), e));

                yield Result.ok(value);
            }
            case Err(Error error) -> {
                yield Result.err(error);
            }
        });
    }
}