package space.nanobreaker.core.domain.v1.todo;

import java.util.Objects;
import java.util.UUID;

public class TodoId {

    private UUID id;

    private String username;

    public TodoId(
            final UUID id,
            final String username
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);

        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
