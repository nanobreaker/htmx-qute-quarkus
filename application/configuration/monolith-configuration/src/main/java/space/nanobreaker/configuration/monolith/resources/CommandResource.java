package space.nanobreaker.configuration.monolith.resources;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

import static space.nanobreaker.configuration.monolith.services.command.Command.Calendar;
import static space.nanobreaker.configuration.monolith.services.command.Command.Help;
import static space.nanobreaker.configuration.monolith.services.command.Command.Todo;
import static space.nanobreaker.configuration.monolith.services.command.Command.User;

import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
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
import org.eclipse.microprofile.jwt.Claim;
import space.nanobreaker.configuration.monolith.services.command.Command;
import space.nanobreaker.configuration.monolith.services.command.CommandExecutor;
import space.nanobreaker.configuration.monolith.services.parser.Parser;
import space.nanobreaker.configuration.monolith.templates.GlobalTemplates;
import space.nanobreaker.library.error.Error;

@Path("commands")
public class CommandResource {

    private final Parser parser;
    private final CommandExecutor executor;

    public CommandResource(
            final Parser parser,
            final CommandExecutor executor
    ) {
        this.parser = parser;
        this.executor = executor;
    }

    @POST
    @Path("submit")
    @WithSpan
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> execute(
            @Claim("upn") String upn,
            @Claim("sid") String sid,
            @CookieParam("time-zone") String zone,
            @FormParam("command") String input
    ) {
        var zoneId = ZoneId.of(URLDecoder.decode(zone, StandardCharsets.UTF_8));
        var parserResult = parser.parse(input);

        return switch (parserResult) {
            // @formatter:off
            case Ok(Command c) -> switch (c) {
                case Help                   _,
                     Todo.Help              _,
                     Todo.Create.Help       _,
                     Todo.List.Help         _,
                     Todo.Update.Help       _,
                     Todo.Delete.Help       _,
                     User.Help              _,
                     Calendar.Help  _       -> executor.help(c);
                case Todo.Create    create  -> executor.todoCreate(create, zoneId);
                case Todo.List      list    -> executor.todoList(list, zoneId);
                case Todo.Update    update  -> executor.todoUpdate(update, zoneId);
                case Todo.Delete    delete  -> executor.todoDelete(delete);
                case Calendar.Show  show    -> executor.calendarShow(show);
                case User.Show      show    -> executor.userShow(show);
            };
            // @formatter:on
            case Err(Error error) -> {
                var text = error.describe();
                var template = new GlobalTemplates.error(text);
                var html = template.render();
                var response = Response.serverError()
                        .entity(html)
                        .build();

                yield Uni.createFrom().item(response);
            }
        };
    }
}
