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
            final Instant startDateTime,
            final Instant endDateTime,
            final String timeZone
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.timeZone = timeZone;
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
        if (startDateTime == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(startDateTime, ZoneId.of(timeZone));
    }

    public ZonedDateTime getEndDateTime() {
        if (endDateTime == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(endDateTime, ZoneId.of(timeZone));
    }

    public int getVersion() {
        return version;
    }
}
