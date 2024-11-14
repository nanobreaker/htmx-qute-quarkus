package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Version;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
public class TodoJpaEntity {

    @Version private Integer version;
    @EmbeddedId private TodoJpaId id;

    private String title;
    private String description;
    private Instant startDateTime;
    private Instant endDateTime;
    private String timeZone;

    public TodoJpaEntity(
            final TodoJpaId id,
            final String title,
            final String description,
            final ZonedDateTime startDateTime,
            final ZonedDateTime endDateTime
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime.toInstant();
        this.endDateTime = endDateTime.toInstant();
        this.timeZone = startDateTime.getZone().getId();
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

    public ZonedDateTime getStartDateTime() {
        return ZonedDateTime.ofInstant(startDateTime, ZoneId.of(timeZone));
    }

    public ZonedDateTime getEndDateTime() {
        return ZonedDateTime.ofInstant(endDateTime, ZoneId.of(timeZone));
    }

    public int getVersion() {
        return version;
    }
}
