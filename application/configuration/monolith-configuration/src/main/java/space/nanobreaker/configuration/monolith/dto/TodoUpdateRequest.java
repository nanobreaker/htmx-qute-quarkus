package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.PathParam;
import space.nanobreaker.library.option.Option;

import java.time.LocalDateTime;

public final class TodoUpdateRequest {

    @CookieParam("time-zone") private String zone;
    @PathParam("id") private Integer id;
    @FormParam("title") private String title;
    @FormParam("description") private String description;
    @FormParam("start") private LocalDateTime start;
    @FormParam("end") private LocalDateTime end;

    public String zone() {
        return zone;
    }

    public Integer id() {
        return id;
    }

    public Option<String> getTitle() {
        return Option.of(title);
    }

    public Option<String> getDescription() {
        return Option.of(description);
    }

    public Option<LocalDateTime> getStart() {
        return Option.of(start);
    }

    public Option<LocalDateTime> getEnd() {
        return Option.of(end);
    }
}