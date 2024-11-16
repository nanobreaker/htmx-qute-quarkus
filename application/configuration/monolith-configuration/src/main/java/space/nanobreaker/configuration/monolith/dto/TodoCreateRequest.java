package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import space.nanobreaker.library.option.Option;

import java.time.LocalDateTime;

public class TodoCreateRequest {

    @CookieParam("time-zone") private String zone;
    @FormParam("title") private String title;
    @FormParam("description") private String description;
    @FormParam("start") private LocalDateTime start;
    @FormParam("end") private LocalDateTime end;

    public String zone() {
        return zone;
    }

    public String title() {
        return title;
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