package space.nanobreaker.configuration.monolith.services.sse;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.keycloak.common.util.ConcurrentMultivaluedHashMap;
import space.nanobreaker.library.tuple.Tuple;

import java.util.function.Predicate;

@ApplicationScoped
public class SseService {

    // todo: how will gc handle closed SseEventSinks?
    private final ConcurrentMultivaluedHashMap<String, Tuple<String, SseEventSink>> connections = new ConcurrentMultivaluedHashMap<>();

    @Context Sse sse;

    @WithSpan("SseService: register")
    public void register(
            final String upn,
            final String sid,
            final SseEventSink eventSink
    ) {
        connections.add(upn, Tuple.of(sid, eventSink));
    }

    @WithSpan("SseService: publish")
    public void publish(
            final String upn,
            final String sid,
            final OutboundSseEvent event
    ) {
        connections.getList(upn)
                .stream()
                .filter(Predicate.not(tuple -> tuple.first().equals(sid)))
                .filter(Predicate.not(tuple -> tuple.second().isClosed()))
                .forEach(sink -> sink.second().send(event));
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
