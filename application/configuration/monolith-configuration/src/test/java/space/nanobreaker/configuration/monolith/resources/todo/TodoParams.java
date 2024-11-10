package space.nanobreaker.configuration.monolith.resources.todo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class TodoParams {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private String title;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;

    static TodoParams params() {
        return new TodoParams();
    }

    TodoParams title(String title) {
        this.title = title;
        return this;
    }

    TodoParams description(String description) {
        this.description = description;
        return this;
    }

    TodoParams start(LocalDateTime start) {
        this.start = start;
        return this;
    }

    TodoParams end(LocalDateTime end) {
        this.end = end;
        return this;
    }

    Map<String, String> build() {
        final var params = new HashMap<String, String>();
        if (Objects.nonNull(title)) {
            params.put("title", title);
        }
        if (Objects.nonNull(description)) {
            params.put("description", description);
        }
        if (Objects.nonNull(start)) {
            params.put("start", start.format(formatter));
        }
        if (Objects.nonNull(end)) {
            params.put("end", end.format(formatter));
        }
        return params;
    }
}