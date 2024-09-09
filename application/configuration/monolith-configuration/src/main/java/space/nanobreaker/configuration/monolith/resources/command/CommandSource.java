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
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.usecases.v1.todo.command.TodoDeleteCommand;
import space.nanobreaker.core.usecases.v1.todo.command.TodoListCommand;

import java.util.stream.Collectors;

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
        final String todoHtml = TodoTemplates.todo(todo, false).render();
        final OutboundSseEvent todoCreated = sse.newEvent("todo.created", todoHtml);
        final String username = todo.getId().getUsername();
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent("empty", ""), username);
        sseEventSink.dispatchOutboundSseEvent(todoCreated, username);
    }

    @ConsumeEvent(value = "todo.updated")
    @WithSpan("handleTodoUpdatedEvent")
    public void handleTodoUpdatedEvent(
            final TodoId todoId
    ) {
        final String username = todoId.getUsername();
        final String key = "todo.updated." + todoId.getId().toString();
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent(key, ""), username);
    }


    @ConsumeEvent(value = "todo.listed")
    @WithSpan("handleTodoListedEvent")
    public void handleTodoListedEvent(
            final TodoListCommand command
    ) {
        final String username = command.username();
        final String key = "todo.listed";
        final String ids = command.ids().stream()
                .map(id -> "id=" + id.toString())
                .collect(Collectors.joining("&"));

        sseEventSink.dispatchOutboundSseEvent(sse.newEvent(key, ids), username);
    }

    @ConsumeEvent(value = "todo.to.delete")
    @WithSpan("handleTodoToDeleteEvent")
    public void handleTodoToDeleteEvent(
            final TodoDeleteCommand command
    ) {
        final TodoId id = command.id();
        final String username = id.getUsername();
        final String key = "todo.to.delete." + id.getId();
        final OutboundSseEvent todoToDelete = sse.newEvent(key, "");
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent("empty", ""), username);
        sseEventSink.dispatchOutboundSseEvent(todoToDelete, username);
    }


    public sealed interface SseCommand permits CommandAnalyzed, CommandClear, CommandError {
    }

    public record CommandAnalyzed(String username, String text) implements SseCommand {
    }

    @ConsumeEvent(value = "command.analyzed")
    @WithSpan("handleCommandAnalyzedEvent")
    public void handleCommandAnalyzedEvent(
            final CommandAnalyzed commandAnalyzed
    ) {
        final String username = commandAnalyzed.username();
        final String key = "help";
        final String data = HelpTemplates.help(commandAnalyzed.text()).render();
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent(key, data), username);
    }

    public record CommandClear(String username) implements SseCommand {
    }

    @ConsumeEvent(value = "command.clear")
    @WithSpan("handleCommandClearEvent")
    public void handleCommandClearEvent(
            final CommandClear commandClear
    ) {
        final String username = commandClear.username();
        final String key = "empty";
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent(key, ""), username);
    }

    public record CommandError(String username, String errorMessage) implements SseCommand {
    }

    @ConsumeEvent(value = "command.error")
    @WithSpan("handleCommandErrorEvent")
    public void handleCommandErrorEvent(
            final CommandError commandError
    ) {
        final String username = commandError.username();
        final String key = "internal_error";
        final String data = ErrorTemplates.error(commandError.errorMessage()).render();
        sseEventSink.dispatchOutboundSseEvent(sse.newEvent(key, data), username);
    }

}
