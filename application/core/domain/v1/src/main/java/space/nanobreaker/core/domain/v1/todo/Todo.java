package space.nanobreaker.core.domain.v1.todo;

import space.nanobreaker.ddd.AggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Todo extends AggregateRoot<TodoId> {

    private String title;
    private String description;
    private TodoState state;
    private LocalDateTime start;
    private LocalDateTime end;

    public Todo(
            final TodoId todoId,
            final String title,
            final String description,
            final TodoState state,
            final LocalDateTime start,
            final LocalDateTime end
    ) {
        Objects.requireNonNull(todoId);
        Objects.requireNonNull(title);

        super(todoId);
        this.title = title;
        this.description = description;
        this.state = state;
        this.start = start;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public TodoState getState() {
        return state;
    }

    public Optional<LocalDateTime> getStart() {
        return Optional.ofNullable(start);
    }

    public Optional<LocalDateTime> getEnd() {
        return Optional.ofNullable(end);
    }
}
