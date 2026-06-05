package dev.thatwhichis.framework.event;

import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.function.Supplier;

import static io.github.dcadea.jresult.Result.err;
import static io.github.dcadea.jresult.Result.ok;

@ApplicationScoped
public class EventDispatcher {

    private final EventBus eventBus;

    @Inject
    public EventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @WithSpan("dispatchEvent")
    public <R> Uni<Result<R, Error>> on(
            final Supplier<Uni<Result<R, Error>>> operation,
            final DomainEvent event
    ) {
        final Uni<Result<R, Error>> resultUni = operation.get();

        return resultUni.flatMap(result -> switch (result) {
            case Ok(R value) -> eventBus
                    .<Result<Void, Error>>request(event.key(), event)
                    .map(Message::body)
                    .map(reply -> switch (reply) {
                        case Ok(_) -> ok(value);
                        case Err(var error) -> err(error);
                    });
            case Err(Error error) -> Uni.createFrom().item(err(error));
        });
    }

    @WithSpan("dispatchEvent")
    public <R> Uni<Result<R, Error>> on(
            final Supplier<Uni<Result<R, Error>>> operation,
            final Set<DomainEvent> events
    ) {
        final Uni<Result<R, Error>> resultUni = operation.get();

        return resultUni.flatMap(result -> switch (result) {
            case Ok(R value) -> {
                var requestsUnis = events
                        .stream()
                        .map(event -> eventBus.<Result<Void, Error>>request(event.key(), event))
                        .toList();

                yield Uni
                        .join()
                        .all(requestsUnis)
                        .andCollectFailures()
                        .map(messages -> messages.stream().map(Message::body).toList())
                        .map(results -> {
                            var errors = results
                                    .stream()
                                    .filter(Result::isErr)
                                    .map(Result::unwrapErr)
                                    .toList();

                            if (errors.isEmpty()) {
                                return Result.<R, Error>ok(value);
                            } else {
                                var error = new DispatchError.DomainEventsDispatchFailed(errors);
                                return Result.<R, Error>err(error);
                            }
                        })
                        .onFailure().recoverWithItem(throwable -> err(new DispatchError.Uncategorized(throwable)));
            }
            case Err(Error error) -> Uni.createFrom().item(err(error));
        });
    }
}