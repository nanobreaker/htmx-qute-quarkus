package dev.thatwhichis.rest.adapter.sse;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.keycloak.common.util.ConcurrentMultivaluedHashMap;

import static java.util.function.Predicate.not;

@ApplicationScoped
public class SseRegistry {

    // @formatter:off
    record SidSinkPair(String sid, SseEventSink sink){}
    // @formatter:on

    // todo: rewrite to local to avoid dependency
    private final ConcurrentMultivaluedHashMap<String, SidSinkPair> connections;

    @Context
    private final Sse sse;

    @Inject
    public SseRegistry(Sse sse) {
        this.sse = sse;
        this.connections = new ConcurrentMultivaluedHashMap<>();
    }

    @WithSpan("register")
    public void register(
            final String upn,
            final String sid,
            final SseEventSink sink
    ) {
        connections.add(upn, new SidSinkPair(sid, sink));
    }

    @WithSpan("publish")
    public void publish(
            final String upn,
            final String sid,
            final OutboundSseEvent event
    ) {
        connections.getList(upn)
                .stream()
                .filter(not(v -> v.sid().equals(sid)))
                .filter(not(v -> v.sink().isClosed()))
                .forEach(v -> v.sink().send(event));
    }

    @ConsumeEvent(value = "sse.todo.created")
    public void consumeTodoCreated(SseEvent.TodoCreated event) {
        final var upn = event.upn();
        final var sid = event.sid();
        final var html = event.html();
        final var sseEvent = sse.newEvent("todo.created", html);

        this.publish(upn, sid, sseEvent);
    }

    @ConsumeEvent(value = "sse.todo.updated")
    public void consumeTodoUpdated(final SseEvent.TodoUpdated event) {
        final var upn = event.upn();
        final var sid = event.sid();
        final var id = event.id();
        final var name = "todo.updated.%s".formatted(id);
        final var sseEvent = sse.newEvent(name, "");

        this.publish(upn, sid, sseEvent);
    }

    @ConsumeEvent(value = "sse.todo.deleted")
    public void consumeTodoDeleted(final SseEvent.TodoDeleted event) {
        final var upn = event.upn();
        final var sid = event.sid();
        final var id = event.id();
        final var name = "todo.deleted.%s".formatted(id);
        final var sseEvent = sse.newEvent(name, "");

        this.publish(upn, sid, sseEvent);
    }
}
