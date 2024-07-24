package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import space.nanobreaker.configuration.monolith.cli.analyzer.Analyzer;
import space.nanobreaker.configuration.monolith.cli.command.*;
import space.nanobreaker.configuration.monolith.cli.parser.Parser;
import space.nanobreaker.configuration.monolith.cli.parser.ParserErr;
import space.nanobreaker.configuration.monolith.cli.tokenizer.TokenizerErr;
import space.nanobreaker.configuration.monolith.sse.SSEService;
import space.nanobreaker.core.usecases.v1.todo.command.TodoCreateCommand;
import space.nanobreaker.library.Err;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Ok;
import space.nanobreaker.library.Result;

@Path("command")
public class CommandResource {

    @Inject
    EventBus eventBus;

    @Context
    Sse sse;

    @Inject
    SSEService sseService;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    Parser parser;

    @Inject
    Analyzer analyzer;

    @POST
    @Path("execute")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response execute(
            @FormParam("command") final String command
    ) {
        final Result<Void, Error> result = switch (parser.parse(command)) {
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
            case Err(Error err) -> Result.err(err);
        };

        if (result.isErr())
            return Response.serverError().build();

        return Response.noContent()
                .build();
    }

    @POST
    @Path("analyze")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    public Response analyze(
            final String command
    ) {
        final String result = switch (analyzer.analyze(command)) {
            case Ok(String help) -> help;
            case Err(Error err) -> describeErr(err);
        };

        updateClient(result);

        return Response.noContent()
                .build();
    }

    @GET
    @Path("feedback")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void eventStream(
            @Context final SseEventSink eventSink
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        sseService.register(username, eventSink);
    }

    private void updateClient(
            final String output
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        sseService.get(username)
                .map(sseEventSink -> sseEventSink.send(sse.newEvent("feedback", output)));
    }

    private Result<Void, Error> executeTodoCreateCommand(
            final CreateTodoCmd cmd
    ) {
        final TodoCreateCommand todoCreateCommand = new TodoCreateCommand(
                securityIdentity.getPrincipal().getName(),
                cmd.title(),
                cmd.description(),
                cmd.start(),
                cmd.end()
        );

        final Message<Result<Void, Error>> response = eventBus
                .requestAndAwait("todo.create", todoCreateCommand);

        return response.body();
    }

    private Result<Void, Error> executeTodoListCommand(
            final ListTodoCmd cmd
    ) {
        return Result.ok(null);
    }

    private Result<Void, Error> executeTodoUpdateCommand(
            final UpdateTodoCmd cmd
    ) {
        return Result.ok(null);
    }

    private Result<Void, Error> executeTodoDeleteCommand(
            final DeleteTodoCmd cmd
    ) {
        return Result.ok(null);
    }

    private Result<Void, Error> executeCalendarShowCommand(
            final CalendarShowCmd cmd
    ) {
        return Result.ok(null);
    }

    private Result<Void, Error> executeUserShowCommand(
            final UserShowCmd cmd
    ) {
        return Result.ok(null);
    }

    private String describeErr(
            final Error err
    ) {
        return switch (err) {
            case ParserErr parserErr -> switch (parserErr) {
                case ParserErr.ArgumentNotFound _ -> "argument required";
                case ParserErr.NotSupportedOperation _ -> "not supported";
                case ParserErr.UnknownCommand _ -> "unknown command";
                case ParserErr.UnknownProgram _ -> "unknown program";
                case ParserErr.DateTimeParseErr e -> STR."can't parse date: \{e.description()}";
            };
            case TokenizerErr tokenizerErr -> switch (tokenizerErr) {
                case TokenizerErr.EmptyInput _ -> "empty command line";
            };
            case CmdErr cmdErr -> switch (cmdErr) {
                case CmdErr.CreationFailed e -> STR."failed to create command: \{e.description()}";
            };
            default -> err.getClass().getTypeName();
        };
    }

}
