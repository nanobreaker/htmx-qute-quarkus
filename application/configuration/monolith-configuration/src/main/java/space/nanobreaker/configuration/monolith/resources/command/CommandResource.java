package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import space.nanobreaker.configuration.monolith.cli.command.CliCommand;
import space.nanobreaker.configuration.monolith.cli.parser.CommandParser;
import space.nanobreaker.configuration.monolith.extension.Err;
import space.nanobreaker.configuration.monolith.extension.Ok;
import space.nanobreaker.configuration.monolith.sse.SSEService;

@Path("cli")
public class CommandResource {

    @Context
    Sse sse;

    @Inject
    SSEService sseService;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    EventBus eventBus;

    @Inject
    CommandParser commandParser;

    @POST
    @Path("process-command")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    public Response process(String command) {
        // todo: rework, handle new type of Errors and make explicit handling of each of them
        final String result = switch (commandParser.parse(command)) {
            case Ok(CliCommand cliCommand) -> cliCommand.help();
            case Err(Exception exception) -> exception.getMessage();
        };

        updateClient(result);

        return Response.noContent()
                .build();
    }

    @POST
    @Path("execute-command")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response execute(@FormParam("command") String command) {
        final String result = switch (commandParser.parse(command)) {
            case Ok(CliCommand cmd) -> cmd.help();
            case Err(Exception exception) -> exception.getMessage();
        };

        updateClient(result);

        return Response.noContent()
                .build();
    }

    @GET
    @Path("feedback")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void eventStream(@Context SseEventSink eventSink) {
        final String username = securityIdentity.getPrincipal().getName();
        sseService.register(username, eventSink);
    }

    private void updateClient(String output) {
        final String username = securityIdentity.getPrincipal().getName();
        sseService.get(username)
                .map(sseEventSink -> sseEventSink.send(sse.newEvent("feedback", output)));
    }

}
