package dev.thatwhichis.rest.adapter.http.command;

import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.rest.adapter.cli.Command;
import dev.thatwhichis.rest.adapter.cli.Command.Logout;
import dev.thatwhichis.rest.adapter.cli.CommandExecutor;
import dev.thatwhichis.rest.adapter.cli.parser.Parser;
import dev.thatwhichis.rest.adapter.qute.templates.ErrorTemplates;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

import static dev.thatwhichis.rest.adapter.cli.Command.Calendar;
import static dev.thatwhichis.rest.adapter.cli.Command.Help;
import static dev.thatwhichis.rest.adapter.cli.Command.Todo;
import static dev.thatwhichis.rest.adapter.cli.Command.User;

@Path("commands")
@Authenticated
public class CommandResource {

    private final Parser parser;
    private final CommandExecutor executor;

    @Inject
    public CommandResource(
            final Parser parser,
            final CommandExecutor executor
    ) {
        this.parser = parser;
        this.executor = executor;
    }

    @POST
    @Path("submit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @WithSpan("submit")
    public Uni<Response> execute(
            @CookieParam("time-zone") final String zone,
            @FormParam("command") final String input
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
                case Calendar.Show  show    -> executor.calendarShow(show, zoneId);
                case User.Show      show    -> executor.userShow(show, zoneId);
                case Logout         _       -> executor.logout();
            };
            // @formatter:on
            case Err(Error error) -> {
                var text = error.describe();
                var template = ErrorTemplates.error(text);

                yield template
                        .createUni()
                        .map(html -> Response
                                .serverError()
                                .entity(html)
                                .build()
                        );
            }
        };
    }
}
