package space.nanobreaker.configuration.monolith.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import space.nanobreaker.library.option.Option;

import java.time.LocalDateTime;

public class TodoCreateRequest {

    @CookieParam("time-zone")
    @NotBlank
    private String zone;

    @FormParam("title")
    @NotBlank
    private String title;

    @FormParam("description")
    private String description;

    @FormParam("start")
    private LocalDateTime start;

    @FormParam("end")
    private LocalDateTime end;

    public String zone() {
        return zone;
    }

    public String title() {
        return title;
    }

    public Option<String> getDescription() {
        return Option.some(description);
    }

    public Option<LocalDateTime> getStart() {
        return Option.some(start);
    }

    public Option<LocalDateTime> getEnd() {
        return Option.some(end);
    }
}