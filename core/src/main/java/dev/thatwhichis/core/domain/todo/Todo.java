package dev.thatwhichis.core.domain.todo;

import dev.thatwhichis.framework.entity.Entity;
import dev.thatwhichis.library.option.Option;

import java.time.Instant;
import java.util.Objects;

public class Todo extends Entity<TodoId> {

    private final String title;
    private final String description;
    private final Instant start;
    private final Instant end;

    public Todo(
            final TodoId todoId,
            final String title,
            final String description,
            final Instant start,
            final Instant end
    ) {
        super(todoId);
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.start = start;
        this.end = end;
    }

    public String getTitle() {
        return this.title;
    }

    public Option<String> getDescription() {
        return Option.some(this.description);
    }

    public Option<Instant> getStart() {
        return Option.some(this.start);
    }

    public Option<Instant> getEnd() {
        return Option.some(this.end);
    }

    public static final class Builder {

        private final TodoId id;
        private final String title;
        private String description;
        private Instant start;
        private Instant end;

        public Builder(
                TodoId id,
                String title
        ) {
            this.id = id;
            this.title = title;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withEnd(Instant ends) {
            this.end = ends;
            return this;
        }

        public Builder withStart(Instant starts) {
            this.start = starts;
            return this;
        }

        public Todo build() {
            return new Todo(id, title, description, start, end);
        }
    }
}
