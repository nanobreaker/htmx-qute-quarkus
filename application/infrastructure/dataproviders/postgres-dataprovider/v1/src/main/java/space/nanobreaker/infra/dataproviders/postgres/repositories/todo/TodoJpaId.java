package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class TodoJpaId implements Serializable {

    @GeneratedValue
    private UUID id;
    private String username;

    public TodoJpaId(
            final UUID id,
            final String username
    ) {
        this.id = id;
        this.username = username;
    }

    public TodoJpaId() {

    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}