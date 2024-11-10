package space.nanobreaker.core.domain.v1.todo;

import space.nanobreaker.ddd.AggregateRoot;
import space.nanobreaker.library.option.Option;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Todo extends AggregateRoot<TodoId> {

    private final String title;
    private final String description;
    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private final TodoState state;

    public Todo(
            final TodoId todoId,
            final String title,
            final String description,
            final ZonedDateTime start,
            final ZonedDateTime end,
            final TodoState state
    ) {
        super(todoId);
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.start = start;
        this.end = end;
        this.state = state;
        // todo: consider moving to the CreateTodoHandler?
        this.registerEvent(new TodoEvent.Created(todoId));
    }

    public String getTitle() {
        return title;
    }

    public Option<String> getDescription() {
        return Option.of(description);
    }

    public Option<ZonedDateTime> getStart() {
        return Option.of(start);
    }

    public Option<ZonedDateTime> getEnd() {
        return Option.of(end);
    }

    public TodoState getState() {
        return state;
    }

    public static final class Builder {

        private final TodoId id;
        private final String title;
        private ZonedDateTime start;
        private ZonedDateTime end;
        private String description;
        private TodoState state;

        public Builder(
                TodoId id,
                String title
        ) {
            this.id = id;
            this.title = title;
        }

        public Builder withState(TodoState state) {
            this.state = state;
            return this;
        }

        public Builder withEnd(ZonedDateTime end) {
            this.end = end;
            return this;
        }

        public Builder withStart(ZonedDateTime start) {
            this.start = start;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Todo build() {
            return new Todo(id, title, description, start, end, state);
        }
    }
}
