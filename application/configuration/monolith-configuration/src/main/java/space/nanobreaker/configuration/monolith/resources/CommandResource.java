package space.nanobreaker.configuration.monolith.resources;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CommandController;
import space.nanobreaker.configuration.monolith.services.parser.Parser;
import space.nanobreaker.configuration.monolith.templates.ErrorTemplates;
import space.nanobreaker.library.error.Error;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

@Path("commands")
public class CommandResource {

    private final Parser parser;
    private final CommandController controller;

    public CommandResource(
            final Parser parser,
            final CommandController controller
    ) {
        this.parser = parser;
        this.controller = controller;
    }

    @POST
    @Path("submit")
    @WithSpan("submit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> execute(
            @CookieParam("time-zone") final String zone,
            @FormParam("command") final String input
    ) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        Result<Command, Error> parserResult = parser.parse(input);

        return switch (parserResult) {
            case Ok(Command c) -> switch (c) {
                case Command.Help help -> controller.help(help);
                case Command.Todo.Help help -> controller.help(help);
                case Command.Todo.Create.Help help -> controller.help(help);
                case Command.Todo.Create.Default def -> controller.createTodo(def, zoneId);
                case Command.Todo.List.Help help -> controller.help(help);
                case Command.Todo.List.All all -> controller.listTodo(all, zoneId);
                case Command.Todo.List.ByIds ids -> controller.listTodo(ids, zoneId);
                case Command.Todo.List.ByFilters filters -> controller.listTodo(filters, zoneId);
                case Command.Todo.List.ByIdsAndFilters idsAndFilters -> controller.listTodo(idsAndFilters, zoneId);
                case Command.Todo.Update.Help help -> controller.help(help);
                case Command.Todo.Update.ByIds ids -> controller.updateTodo(ids, zoneId);
                case Command.Todo.Update.ByFilters filters -> controller.updateTodo(filters, zoneId);
                case Command.Todo.Update.ByIdsAndFilters idsAndFilters -> controller.updateTodo(idsAndFilters, zoneId);
                case Command.Todo.Delete.Help help -> controller.help(help);
                case Command.Todo.Delete.All all -> controller.deleteTodos(all);
                case Command.Todo.Delete.ByIds ids -> controller.deleteTodos(ids);
                case Command.Calendar.Help help -> controller.help(help);
                case Command.Calendar.Show show -> controller.showCalendar(show);
                case Command.User.Help help -> controller.help(help);
                case Command.User.Show show -> controller.showUser(show);
            };
            case Err(Error error) -> {
                var text = error.describe();
                var html = ErrorTemplates.error(text);

                var response = Response.serverError()
                        .entity(html)
                        .build();

                yield Uni.createFrom().item(response);
            }
        };
    }
}
