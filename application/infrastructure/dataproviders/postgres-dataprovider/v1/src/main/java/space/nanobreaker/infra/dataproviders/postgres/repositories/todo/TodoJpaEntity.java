package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Version;
import space.nanobreaker.core.domain.v1.todo.TodoState;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
public class TodoJpaEntity {

    @Version private Integer version;
    @EmbeddedId private TodoJpaId id;

    private String title;
    private String description;
    private TodoState state;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;

    public TodoJpaEntity(
            final TodoJpaId id,
            final String title,
            final String description,
            final TodoState state,
            final ZonedDateTime startDateTime,
            final ZonedDateTime endDateTime
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.state = state;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public TodoJpaEntity() {

    }

    public TodoJpaId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TodoState getState() {
        return state;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public int getVersion() {
        return version;
    }
}
