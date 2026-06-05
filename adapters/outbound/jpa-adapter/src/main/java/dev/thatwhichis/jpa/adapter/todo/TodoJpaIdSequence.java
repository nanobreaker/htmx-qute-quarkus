package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class TodoJpaIdSequence {

    @Id
    private UUID userId;

    private Integer seq;

    public void setSeq(Integer id) {
        this.seq = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getSeq() {
        return seq;
    }

    public UUID getUserId() {
        return userId;
    }

    public TodoId into() {
        return new TodoId(this.getSeq(), this.getUserId());
    }
}