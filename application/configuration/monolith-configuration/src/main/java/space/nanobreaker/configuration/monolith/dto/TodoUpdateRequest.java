package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.PathParam;
import space.nanobreaker.library.option.Option;

import java.time.LocalDateTime;

public record TodoUpdateRequest(
        @CookieParam("time-zone") String zone,
        @PathParam("id") Integer id,
        @FormParam("title") String title,
        @FormParam("description") String description,
        @FormParam("start") LocalDateTime start,
        @FormParam("end") LocalDateTime end
) {

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