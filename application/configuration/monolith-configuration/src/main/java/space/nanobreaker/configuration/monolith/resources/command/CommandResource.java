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
import jakarta.ws.rs.sse.Sse;
import space.nanobreaker.configuration.monolith.services.analyzer.Analyzer;
import space.nanobreaker.configuration.monolith.services.command.*;
import space.nanobreaker.configuration.monolith.services.parser.Parser;
import space.nanobreaker.configuration.monolith.services.parser.ParserErr;
import space.nanobreaker.configuration.monolith.services.tokenizer.TokenizerErr;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.usecases.v1.todo.command.TodoCreateCommand;
import space.nanobreaker.core.usecases.v1.todo.command.TodoDeleteCommand;
import space.nanobreaker.core.usecases.v1.todo.command.TodoListCommand;
import space.nanobreaker.core.usecases.v1.todo.command.TodoUpdateCommand;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Path("command")
public class CommandResource {

    @Inject
    EventBus eventBus;

    @Context
    Sse sse;

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
        final Result<Command, Error> commandResult = parser.parse(command);

        return switch (commandResult) {
            case Ok(Command cmd) -> switch (cmd) {
                case TodoCmd todoCmd -> switch (todoCmd) {
                    case CreateTodoCmd createTodoCmd -> this.execute(createTodoCmd);
                    case UpdateTodoCmd updateTodoCmd -> this.execute(updateTodoCmd);
                    case ListTodoCmd listTodoCmd -> this.execute(listTodoCmd);
                    case DeleteTodoCmd deleteTodoCmd -> this.execute(deleteTodoCmd);
                };
                case CalendarCmd calendarCmd -> switch (calendarCmd) {
                    case CalendarShowCmd calendarShowCmd -> this.execute(calendarShowCmd);
                };
                case UserCmd userCmd -> switch (userCmd) {
                    case UserShowCmd userShowCmd -> this.execute(userShowCmd);
                };
            };
            case Err(Error err) -> Uni.createFrom()
                    .item(
                            Response.serverError()
                                    .entity(error.data("error", this.describe(err)))
                                    .build()
                    );
        };
    }

    @POST
    @Path("analyze")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_HTML)
    @WithSpan("analyze")
    public Response analyze(
            final String command
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        final CommandSource.SseCommand sseCommand = switch (analyzer.analyze(command)) {
            case Ok(final String help) -> new CommandSource.CommandAnalyzed(username, help);
            case Err(final Error err) -> switch (err) {
                case TokenizerErr tokenizerErr -> switch (tokenizerErr) {
                    case TokenizerErr.EmptyInput ignored -> new CommandSource.CommandClear(username);
                };
                default -> new CommandSource.CommandError(username, this.describe(err));
            };
        };

        switch (sseCommand) {
            case CommandSource.CommandAnalyzed c -> eventBus.publish("command.analyzed", c);
            case CommandSource.CommandClear c -> eventBus.publish("command.clear", c);
            case CommandSource.CommandError c -> eventBus.publish("command.error", c);
        }

        return Response.noContent()
                .build();
    }

    private Uni<Response> execute(
            final CreateTodoCmd cmd
    ) {
        final TodoCreateCommand command = new TodoCreateCommand(
                securityIdentity.getPrincipal().getName(),
                cmd.title(),
                cmd.description(),
                Objects.nonNull(cmd.start()) ? cmd.start().toDateTime() : null,
                Objects.nonNull(cmd.end()) ? cmd.end().toDateTime() : null
        );

        final Uni<Result<TodoId, Error>> result = eventBus
                .<Result<TodoId, Error>>request("todo.create", command)
                .map(Message::body);

        return result.map(r -> switch (r) {
            case Ok(TodoId todoId) -> Response
                    .created(URI.create(todoId.getId().toString()))
                    .build();
            case Err(Error err) -> Response
                    .serverError()
                    .entity(error.data("error", err.getClass().getName()))
                    .build();
        });
    }

    private Uni<Response> execute(
            final UpdateTodoCmd cmd
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        final Option<Set<TodoId>> idsOption = Option.of(cmd.ids())
                .map(ids -> ids.stream()
                        .map(id -> new TodoId(id, username))
                        .collect(Collectors.toSet())
                );

        final TodoUpdateCommand todoUpdateCommand = new TodoUpdateCommand(
                username,
                idsOption,
                Option.of(cmd.filters()),
                Option.of(cmd.title()),
                Option.of(cmd.description()),
                Objects.nonNull(cmd.start()) ? Option.of(cmd.start().toDateTime()) : Option.none(),
                Objects.nonNull(cmd.end()) ? Option.of(cmd.end().toDateTime()) : Option.none()
        );

        final Uni<Result<Void, Error>> result = eventBus
                .<Result<Void, Error>>request("todo.update", todoUpdateCommand)
                .map(Message::body);

        return result.map(r -> switch (r) {
            case Ok(Void ignored) -> Response
                    .accepted()
                    .build();
            case Err(Error err) -> Response
                    .serverError()
                    .entity(error.data("error", err.getClass().getName()))
                    .build();
        });
    }

    private Uni<Response> execute(
            final ListTodoCmd cmd
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        final TodoListCommand todoListCommand = new TodoListCommand(username, cmd.ids());

        final Uni<Result<Void, Error>> result = eventBus
                .<Result<Void, Error>>request("todo.list", todoListCommand)
                .map(Message::body);

        return result.map(r -> switch (r) {
            case Ok(Void ignored) -> Response
                    .ok()
                    .build();
            case Err(Error err) -> Response
                    .serverError()
                    .entity(error.data("error", err.getClass().getName()))
                    .build();
        });
    }

    private Uni<Response> execute(
            final DeleteTodoCmd cmd
    ) {
        final String username = securityIdentity.getPrincipal().getName();
        final Set<TodoDeleteCommand> commands = cmd.ids().stream()
                .map(id -> new TodoDeleteCommand(new TodoId(id, username)))
                .collect(Collectors.toSet());

        final List<Uni<Void>> requests = commands.stream()
                .map(command ->
                        Uni.createFrom()
                                .item(() -> eventBus.publish("todo.to.delete", command))
                                .replaceWithVoid()
                )
                .toList();

        return Uni.join()
                .all(requests)
                .andCollectFailures()
                .map(ignored -> Response.ok().build());
    }

    private Uni<Response> execute(
            final CalendarShowCmd cmd
    ) {
        return Uni.createFrom().item(Response.serverError().build());
    }

    private Uni<Response> execute(
            final UserShowCmd cmd
    ) {
        return Uni.createFrom().item(Response.serverError().build());
    }

    private String describe(
            final Error err
    ) {
        return switch (err) {
            case ParserErr parserErr -> switch (parserErr) {
                case ParserErr.ArgumentNotFound ignored -> "argument required";
                case ParserErr.NotSupportedOperation ignored -> "not supported";
                case ParserErr.UnknownCommand ignored -> "unknown command";
                case ParserErr.UnknownProgram ignored -> "unknown program";
                case ParserErr.FailedToParseDate e -> "can't parse date: " + e.description();
            };
            case TokenizerErr tokenizerErr -> switch (tokenizerErr) {
                case TokenizerErr.EmptyInput ignored -> "empty command line";
            };
            case CmdErr cmdErr -> switch (cmdErr) {
                case CmdErr.CreationFailed e -> "failed to create command: " + e.description();
            };
            default -> err.getClass().getTypeName();
        };
    }

}
