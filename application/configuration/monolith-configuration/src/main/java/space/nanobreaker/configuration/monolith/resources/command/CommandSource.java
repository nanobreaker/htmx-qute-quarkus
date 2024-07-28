package space.nanobreaker.configuration.monolith.resources.command;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import space.nanobreaker.configuration.monolith.resources.todo.TodoTemplates;
import space.nanobreaker.configuration.monolith.sse.SseEventSinkService;
import space.nanobreaker.core.domain.v1.todo.Todo;

@Path("command")
public class CommandSource {

    @Context
    Sse sse;

    @Inject
    SseEventSinkService sseEventSink;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("feedback")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void register(
            @Context final SseEventSink eventSink
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        sseEventSink.register(username, eventSink);
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent("open"), username);
    }

    @ConsumeEvent(value = "todo.created")
    @WithSpan("handleTodoCreatedEvent")
    public void handleTodoCreatedEvent(
            final Todo todo
    ) {
        final String todoHtml = TodoTemplates.todo(todo).render();
        final OutboundSseEvent todoCreated = sse.newEvent("todoCreated", todoHtml);
        final String username = todo.getId().getUsername();
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent("empty", ""), username);
        sseEventSink.dispatchOutboundSseEvent(todoCreated, username);
    }

}
