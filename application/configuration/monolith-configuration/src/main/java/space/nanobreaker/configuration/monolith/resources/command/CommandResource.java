package space.nanobreaker.configuration.monolith.resources.command;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import space.nanobreaker.configuration.monolith.services.analyzer.Analyzer;
import space.nanobreaker.configuration.monolith.services.command.*;
import space.nanobreaker.configuration.monolith.services.parser.Parser;
import space.nanobreaker.configuration.monolith.services.parser.ParserErr;
import space.nanobreaker.configuration.monolith.services.tokenizer.TokenizerErr;
import space.nanobreaker.configuration.monolith.sse.SseEventSinkService;
import space.nanobreaker.core.usecases.v1.todo.command.TodoCreateCommand;
import space.nanobreaker.library.Err;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Ok;
import space.nanobreaker.library.Result;

import java.net.URI;

@Path("command")
public class CommandResource {

    @Inject
    EventBus eventBus;

    @Context
    Sse sse;

    @Inject
    SseEventSinkService sseEventSink;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    Parser parser;

    @Inject
    Analyzer analyzer;

    @Location("exception/error.qute.html")
    Template error;

    @POST
    @Path("execute")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @WithSpan("execute")
    public Uni<Response> execute(
            @FormParam("command") final String command
    ) {
        final Uni<Result<Void, Error>> result = switch (parser.parse(command)) {
            case Ok(Command cmd) -> switch (cmd) {
                case TodoCmd todoCmd -> switch (todoCmd) {
                    case CreateTodoCmd createTodoCmd -> executeTodoCreateCommand(createTodoCmd);
                    case DeleteTodoCmd deleteTodoCmd -> executeTodoDeleteCommand(deleteTodoCmd);
                    case ListTodoCmd listTodoCmd -> executeTodoListCommand(listTodoCmd);
                    case UpdateTodoCmd updateTodoCmd -> executeTodoUpdateCommand(updateTodoCmd);
                };
                case CalendarCmd calendarCmd -> switch (calendarCmd) {
                    case CalendarShowCmd calendarShowCmd -> executeCalendarShowCommand(calendarShowCmd);
                };
                case UserCmd userCmd -> switch (userCmd) {
                    case UserShowCmd userShowCmd -> executeUserShowCommand(userShowCmd);
                };
            };
            case Err(Error err) -> Uni.createFrom().item(Result.err(err));
        };

        return result.map(r -> switch (r) {
            case Ok(Void _) -> Response.created(URI.create("test")).build();
            case Err(Error err) -> Response.serverError()
                    .entity(error.data("error", err.getClass().getName()))
                    .build();
        });
    }

    @POST
    @Path("analyze")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    @WithSpan("analyze")
    public Response analyze(
            final String command
    ) {
        final OutboundSseEvent event = switch (analyzer.analyze(command)) {
            case Ok(String help) -> sse.newEvent("help", help);
            case Err(Error err) -> switch (err) {
                case TokenizerErr tokenizerErr -> switch (tokenizerErr) {
                    case TokenizerErr.EmptyInput _ -> sse.newEvent("empty", "");
                };
                default -> sse.newEvent("internal_error", mapErrorToDescription(err));
            };
        };

        final String username = securityIdentity.getPrincipal().getName();
        sseEventSink.dispatchOutboundSseEvent(event, username);

        return Response.noContent()
                .build();
    }

    private Uni<Result<Void, Error>> executeTodoCreateCommand(
            final CreateTodoCmd cmd
    ) {
        final TodoCreateCommand todoCreateCommand = new TodoCreateCommand(
                securityIdentity.getPrincipal().getName(),
                cmd.title(),
                cmd.description(),
                cmd.start(),
                cmd.end()
        );

        return eventBus.<Result<Void, Error>>request("todo.create", todoCreateCommand)
                .map(Message::body);
    }

    private Uni<Result<Void, Error>> executeTodoListCommand(
            final ListTodoCmd cmd
    ) {
        return Uni.createFrom().item(Result.ok(null));
    }

    private Uni<Result<Void, Error>> executeTodoUpdateCommand(
            final UpdateTodoCmd cmd
    ) {
        return Uni.createFrom().item(Result.ok(null));
    }

    private Uni<Result<Void, Error>> executeTodoDeleteCommand(
            final DeleteTodoCmd cmd
    ) {
        return Uni.createFrom().item(Result.ok(null));
    }

    private Uni<Result<Void, Error>> executeCalendarShowCommand(
            final CalendarShowCmd cmd
    ) {
        return Uni.createFrom().item(Result.ok(null));
    }

    private Uni<Result<Void, Error>> executeUserShowCommand(
            final UserShowCmd cmd
    ) {
        return Uni.createFrom().item(Result.ok(null));
    }

    private String mapErrorToDescription(
            final Error err
    ) {
        return switch (err) {
            case ParserErr parserErr -> switch (parserErr) {
                case ParserErr.ArgumentNotFound _ -> "argument required";
                case ParserErr.NotSupportedOperation _ -> "not supported";
                case ParserErr.UnknownCommand _ -> "unknown command";
                case ParserErr.UnknownProgram _ -> "unknown program";
                case ParserErr.DateTimeParseErr e -> "can't parse date: " + e.description();
            };
            case TokenizerErr tokenizerErr -> switch (tokenizerErr) {
                case TokenizerErr.EmptyInput _ -> "empty command line";
            };
            case CmdErr cmdErr -> switch (cmdErr) {
                case CmdErr.CreationFailed e -> "failed to create command: " + e.description();
            };
            default -> err.getClass().getTypeName();
        };
    }

}
