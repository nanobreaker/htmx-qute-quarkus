package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.PathParam;
import space.nanobreaker.configuration.monolith.services.command.EndDateTime;
import space.nanobreaker.configuration.monolith.services.command.StartDateTime;
import space.nanobreaker.library.option.Option;

public class TodoUpdateRequest {

    @PathParam("id")
    Integer id;

    @FormParam("title")
    String title;

    @FormParam("description")
    String description;

    @FormParam("start")
    StartDateTime start;

    @FormParam("end")
    EndDateTime end;

    public Option<Integer> id() {
        return Option.of(id);
    }

    public Option<String> title() {
        return Option.of(title);
    }

    public Option<String> description() {
        return Option.of(description);
    }

    public Option<StartDateTime> start() {
        return Option.of(start);
    }

    public Option<EndDateTime> end() {
        return Option.of(end);
    }
}