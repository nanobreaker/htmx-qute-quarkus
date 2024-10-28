package space.nanobreaker.configuration.monolith.resources;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
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
import space.nanobreaker.library.result.Err;
import space.nanobreaker.library.result.Ok;
import space.nanobreaker.library.result.Result;

import java.net.URI;
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
    public Uni<Response> execute(@FormParam("command") final String input) {
        final String upn = jwt.getClaim("upn");
        final String sid = jwt.getClaim("sid");

        final Result<Command, Error> parserResult = parser.parse(input);

        return switch (parserResult) {
            case Ok(Command c) -> switch (c) {
                case TodoCommand tc -> switch (tc) {
                    case CreateTodoCommand command -> executor
                            .createTodo(command)
                            .map(result -> switch (result) {
                                case Ok(Todo todo) -> {
                                    final var location = "/todos/%d".formatted(todo.getId().getId());
                                    final var uri = URI.create(location);
                                    // todo: consider using 'item' fragment from todos template?
                                    final var html = TodoTemplates.todo(todo);
                                    final var event = new SseEvent.TodoCreated(upn, sid, html.render());
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
                                    final String html = TodoTemplates.todos(todos)
                                            .getFragment("items")
                                            .instance()
                                            .data("todos", todos)
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
                            .updateTodo(updateTodoCommand)
                            .map(result -> switch (result) {
                                case Ok(Void _) -> switch (updateTodoCommand.ids()) {
                                    case Some(Set<Integer> ids) -> {
                                        final var query = ids.stream()
                                                .map("id=%d"::formatted)
                                                .collect(Collectors.joining("&"));
                                        final var location = URI.create("/todos/search?%s".formatted(query));

                                        ids.forEach(id -> {
                                            final var event = new SseEvent.TodoUpdated(upn, sid, id);
                                            eventBus.publish("sse.todo.updated", event);
                                        });

                                        yield Response.seeOther(location)
                                                .build();
                                    }
                                    case None() -> {
                                        final var location = URI.create("/todos/search");

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
                                    final var ids = deleteTodoCommand.ids();

                                    ids.forEach(id -> {
                                        final var event = new SseEvent.TodoDeleted(upn, sid, id);
                                        eventBus.publish("sse.todo.deleted", event);
                                    });

                                    final var html = TodoTemplates.todosDelete(ids)
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
                final var html = HelpTemplates.help(help);

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
                        final var html = ErrorTemplates.error(err.describe());

                        yield Response.serverError()
                                .entity(html)
                                .build();
                    }
                }
            }
        };
    }
}
