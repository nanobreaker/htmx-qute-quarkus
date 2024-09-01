package space.nanobreaker.configuration.monolith.sse;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.SseEventSink;
import space.nanobreaker.library.None;
import space.nanobreaker.library.Option;
import space.nanobreaker.library.Some;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SseEventSinkService {

    private final ConcurrentHashMap<String, SseEventSink> sseEventSinks = new ConcurrentHashMap<>();

    public void register(String principal, SseEventSink eventSink) {
        sseEventSinks.put(principal, eventSink);
    }

    public Optional<SseEventSink> get(String principal) {
        final SseEventSink sseEventSink = sseEventSinks.get(principal);
        if (sseEventSink == null || sseEventSink.isClosed()) {
            return Optional.empty();
        }

        return Optional.of(sseEventSink);
    }

    @WithSpan("dispatchOutboundSseEvent")
    public void dispatchOutboundSseEvent(
            final OutboundSseEvent event,
            final String username
    ) {
        final Option<SseEventSink> optionalSink = Option.of(get(username));

        switch (optionalSink) {
            case Some(SseEventSink sink) -> sink.send(event);
            case None() -> Log.warn("sink not found for " + username);
        }
    }
}
