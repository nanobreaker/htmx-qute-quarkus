package space.nanobreaker.configuration.monolith.services.sse;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.keycloak.common.util.ConcurrentMultivaluedHashMap;

import java.util.function.Predicate;

@ApplicationScoped
public class SseService {

    // @formatter:off
    record SidSinkPair(String sid, SseEventSink sink){}
    // @formatter:on
    private final ConcurrentMultivaluedHashMap<String, SidSinkPair> connections;

    @Context
    private final Sse sse;

    public SseService(Sse sse) {
        this.sse = sse;
        this.connections = new ConcurrentMultivaluedHashMap<>();
    }

    @WithSpan("SseService: register")
    public void register(
            final String upn,
            final String sid,
            final SseEventSink sink
    ) {
        connections.add(upn, new SidSinkPair(sid, sink));
    }

    @WithSpan("SseService: publish")
    public void publish(
            final String upn,
            final String sid,
            final OutboundSseEvent event
    ) {
        connections.getList(upn)
                .stream()
                .filter(Predicate.not(v -> v.sid().equals(sid)))
                .filter(Predicate.not(v -> v.sink().isClosed()))
                .forEach(v -> v.sink().send(event));
    }

    @ConsumeEvent(value = "sse.todo.created")
    @WithSpan("SseService: consumeTodoCreated")
    public void consumeTodoCreated(SseEvent.TodoCreated event) {
        final var upn = event.upn();
        final var sid = event.sid();
        final var html = event.html();
        final var sseEvent = sse.newEvent("todo.created", html);

        this.publish(upn, sid, sseEvent);
    }

    @ConsumeEvent(value = "sse.todo.updated")
    @WithSpan("SseService: consumeTodoUpdated")
    public void consumeTodoUpdated(final SseEvent.TodoUpdated event) {
        final var upn = event.upn();
        final var sid = event.sid();
        final var id = event.id();
        final var name = "todo.updated.%s".formatted(id);
        final var sseEvent = sse.newEvent(name, "");

        this.publish(upn, sid, sseEvent);
    }

    @ConsumeEvent(value = "sse.todo.deleted")
    @WithSpan("SseService: consumeTodoDeleted")
    public void consumeTodoDeleted(final SseEvent.TodoDeleted event) {
        final var upn = event.upn();
        final var sid = event.sid();
        final var id = event.id();
        final var name = "todo.deleted.%s".formatted(id);
        final var sseEvent = sse.newEvent(name, "");

        this.publish(upn, sid, sseEvent);
    }
}
