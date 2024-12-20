package space.nanobreaker.configuration.monolith.resources;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import space.nanobreaker.configuration.monolith.services.analyzer.Analyzer;
import space.nanobreaker.configuration.monolith.services.command.CalendarCommand;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CreateTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.DeleteTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.Executor;
import space.nanobreaker.configuration.monolith.services.command.ListTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.ShowCalendarCommand;
import space.nanobreaker.configuration.monolith.services.command.ShowUserCommand;
import space.nanobreaker.configuration.monolith.services.command.TodoCommand;
import space.nanobreaker.configuration.monolith.services.command.UpdateTodoCommand;
import space.nanobreaker.configuration.monolith.services.command.UserCommand;
import space.nanobreaker.configuration.monolith.services.parser.Parser;
import space.nanobreaker.configuration.monolith.services.sse.SseEvent;
import space.nanobreaker.configuration.monolith.services.tokenizer.TokenizerError;
import space.nanobreaker.configuration.monolith.templates.ErrorTemplates;
import space.nanobreaker.configuration.monolith.templates.HelpTemplates;
import space.nanobreaker.configuration.monolith.templates.TodoTemplates;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.None;
import space.nanobreaker.library.option.Some;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@Path("commands")
public class CommandResource {

    @Inject JsonWebToken jwt;
    @Inject EventBus eventBus;
    @Inject Parser parser;
    @Inject Analyzer analyzer;
    @Inject Executor executor;

    @POST
    @Path("submit")
    @WithSpan("submit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> execute(
            @CookieParam("time-zone") final String zone,
            @FormParam("command") final String input
    ) {
        final var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        final String upn = jwt.getClaim("upn");
        final String sid = jwt.getClaim("sid");

        final Result<Command, Error> parserResult = parser.parse(input);

        return switch (parserResult) {
            case Ok(Command c) -> switch (c) {
                case TodoCommand tc -> switch (tc) {
                    case CreateTodoCommand command -> executor
                            .createTodo(command, zoneId)
                            .map(result -> switch (result) {
                                case Ok(Todo todo) -> {
                                    var location = "/todos/%d".formatted(todo.getId().getId());
                                    var uri = URI.create(location);
                                    // todo: consider using 'item' fragment from todos template?
                                    var html = TodoTemplates.todo(todo, zoneId).render();
                                    var event = new SseEvent.TodoCreated(upn, sid, html);
                                    eventBus.publish("sse.todo.created", event);

                                    yield Response.created(uri)
                                            .header("HX-Reswap", "beforeend")
                                            .header("HX-Trigger", "command.empty")
                                            .entity(html)
                                            .build();
                                }
                                case Err(Error err) -> {
                                    yield Response.serverError()
                                            .entity(ErrorTemplates.error(err.describe()))
                                            .build();
                                }
                            });
                    case ListTodoCommand listTodoCommand -> executor
                            .listTodo(listTodoCommand)
                            .map(result -> switch (result) {
                                case Ok(Set<Todo> todos) -> {
                                    var html = TodoTemplates.todos(todos, zoneId)
                                            .getFragment("items")
                                            .instance()
                                            .data("todos", todos)
                                            .data("zoneId", zoneId)
                                            .render();

                                    yield Response.ok()
                                            .header("HX-Trigger", "command.empty")
                                            .entity(html)
                                            .build();
                                }
                                case Err(Error err) -> {
                                    yield Response.serverError()
                                            .entity(ErrorTemplates.error(err.describe()))
                                            .build();
                                }
                            });
                    case UpdateTodoCommand updateTodoCommand -> executor
                            .updateTodo(updateTodoCommand, zoneId)
                            .map(result -> switch (result) {
                                case Ok(Void _) -> switch (updateTodoCommand.ids()) {
                                    case Some(Set<Integer> ids) -> {
                                        var query = ids.stream()
                                                .map("id=%d"::formatted)
                                                .collect(Collectors.joining("&"));
                                        var location = URI.create("/todos/search?%s".formatted(query));

                                        ids.forEach(id -> {
                                            final var event = new SseEvent.TodoUpdated(upn, sid, id);
                                            eventBus.publish("sse.todo.updated", event);
                                        });

                                        yield Response.seeOther(location)
                                                .build();
                                    }
                                    case None() -> {
                                        var location = URI.create("/todos/search");

                                        yield Response.seeOther(location)
                                                .header("HX-Trigger", "command.empty")
                                                .build();
                                    }
                                };
                                case Err(Error err) -> {
                                    yield Response.serverError()
                                            .entity(ErrorTemplates.error(err.describe()))
                                            .build();
                                }
                            });
                    case DeleteTodoCommand deleteTodoCommand -> executor
                            .deleteTodos(deleteTodoCommand)
                            .map(result -> switch (result) {
                                case Ok(_) -> {
                                    var ids = deleteTodoCommand.ids();

                                    ids.forEach(id -> {
                                        var event = new SseEvent.TodoDeleted(upn, sid, id);
                                        eventBus.publish("sse.todo.deleted", event);
                                    });

                                    var html = TodoTemplates.todosDelete(ids)
                                            .render();

                                    yield Response.ok(html)
                                            .header("HX-Reswap", "none")
                                            .header("HX-Trigger", "command.empty")
                                            .build();
                                }
                                case Err(Error err) -> {
                                    yield Response.serverError()
                                            .entity(ErrorTemplates.error(err.describe()))
                                            .build();
                                }
                            });
                };
                case CalendarCommand calendarCommand -> switch (calendarCommand) {
                    case ShowCalendarCommand showCalendarCommand -> executor
                            .showCalendar(showCalendarCommand)
                            .map(result -> switch (result) {
                                case Ok(_) -> {
                                    // todo: implement show calendar command
                                    yield Response.accepted()
                                            .header("HX-Trigger", "command.empty")
                                            .build();
                                }
                                case Err(Error err) -> {
                                    yield Response.serverError()
                                            .entity(ErrorTemplates.error(err.describe()))
                                            .build();
                                }
                            });
                };
                case UserCommand userCommand -> switch (userCommand) {
                    case ShowUserCommand showUserCommand -> executor
                            .showUser(showUserCommand)
                            .map(result -> switch (result) {
                                case Ok(_) -> {
                                    // todo: implement show user command
                                    yield Response.accepted()
                                            .header("HX-Trigger", "command.empty")
                                            .build();
                                }
                                case Err(Error err) -> {
                                    yield Response.serverError()
                                            .entity(ErrorTemplates.error(err.describe()))
                                            .build();
                                }
                            });
                };
            };
            case Err(Error error) -> {
                yield Uni.createFrom()
                        .item(
                                Response.serverError()
                                        .entity(ErrorTemplates.error(error.describe()))
                                        .build()
                        );
            }
        };
    }

    @POST
    @Path("validate")
    @WithSpan("validate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response analyze(@FormParam("command") String command) {
        return switch (analyzer.analyze(command)) {
            case Ok(String help) -> {
                var html = HelpTemplates.help(help);

                yield Response.ok(html)
                        .build();
            }
            case Err(Error err) -> {
                switch (err) {
                    case TokenizerError.EmptyInput _ -> {
                        yield Response.ok()
                                .header("HX-Trigger", "command.empty")
                                .build();
                    }
                    default -> {
                        var html = ErrorTemplates.error(err.describe());

                        yield Response.serverError()
                                .entity(html)
                                .build();
                    }
                }
            }
        };
    }
}
