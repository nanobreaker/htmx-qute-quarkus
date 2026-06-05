package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
public class TodoJpaEntity {

    @Version
    private Integer version;

    @EmbeddedId
    private TodoJpaId id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_timestamp")
    private Instant start;

    @Column(name = "end_timestamp")
    private Instant end;

    public TodoJpaEntity(
            final TodoJpaId id,
            final String title,
            final String description,
            final Instant start,
            final Instant end
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
    }

    public TodoJpaEntity() {

    }

    public TodoJpaId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Todo into() {
        var id = this.id.into();

        return new Todo(
                id,
                this.getTitle(),
                this.getDescription(),
                this.getStart(),
                this.getEnd()
        );
    }

    public static TodoJpaEntity from(final Todo todo) {
        var id = TodoJpaId.from(todo.getId());

        return new TodoJpaEntity(
                id,
                todo.getTitle(),
                todo.getDescription().orElse(null),
                todo.getStart().orElse(null),
                todo.getEnd().orElse(null)
        );
    }
}
