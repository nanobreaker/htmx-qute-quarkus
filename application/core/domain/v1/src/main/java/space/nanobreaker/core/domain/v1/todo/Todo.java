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
        super(todoId);
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.state = state;
        this.start = start;
        this.end = end;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setState(TodoState state) {
        this.state = state;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
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
