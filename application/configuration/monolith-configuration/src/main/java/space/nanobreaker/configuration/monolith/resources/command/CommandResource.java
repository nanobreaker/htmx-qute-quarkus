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
import space.nanobreaker.configuration.monolith.cli.command.CmdErr;
import space.nanobreaker.configuration.monolith.cli.command.Command;
import space.nanobreaker.configuration.monolith.cli.parser.Parser;
import space.nanobreaker.configuration.monolith.cli.parser.ParserErr;
import space.nanobreaker.configuration.monolith.cli.tokenizer.TokenizerErr;
import space.nanobreaker.configuration.monolith.extension.Err;
import space.nanobreaker.configuration.monolith.extension.Error;
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
    Parser parser;

    @POST
    @Path("process-command")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    public Response process(String command) {
        final String result = switch (parser.parse(command)) {
            case Ok(Command cmd) -> cmd.help();
            case Err(Error err) -> describeErr(err);
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
        switch (parser.parse(command)) {
            case Ok(Command cmd) -> cmd.help();
            case Err(Error err) -> err.getClass().getTypeName();
        }

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

    private String describeErr(final Error err) {
        return switch (err) {
            case ParserErr parserErr -> switch (parserErr) {
                case ParserErr.ArgumentNotFound _ -> "argument required";
                case ParserErr.NotSupportedOperation _ -> "not supported";
                case ParserErr.UnknownCommand _ -> "unknown command";
                case ParserErr.UnknownProgram _ -> "unknown program";
            };
            case TokenizerErr tokenizerErr -> switch (tokenizerErr) {
                case TokenizerErr.EmptyInput _ -> "empty command line";
            };
            case CmdErr cmdErr -> switch (cmdErr) {
                case CmdErr.CreationFailed creationFailed ->
                        STR."failed to create command: \{creationFailed.description()}";
            };
            default -> err.getClass().getTypeName();
        };
    }

}
