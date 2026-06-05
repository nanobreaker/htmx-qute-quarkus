package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class TodoJpaId implements Serializable {

    private Integer id;
    private UUID userId;

    public TodoJpaId(
            final Integer id,
            final UUID userId
    ) {
        this.id = id;
        this.userId = userId;
    }

    public TodoJpaId() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public TodoId into() {
        return new TodoId(this.id, this.userId);
    }

    public static TodoJpaId from(TodoId id) {
        return new TodoJpaId(id.id(), id.userId());
    }
}