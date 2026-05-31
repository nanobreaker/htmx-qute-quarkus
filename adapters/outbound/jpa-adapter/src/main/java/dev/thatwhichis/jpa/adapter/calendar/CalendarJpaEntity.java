package dev.thatwhichis.jpa.adapter.calendar;

import dev.thatwhichis.core.domain.calendar.Calendar;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class CalendarJpaEntity {

    @Version
    private Integer version;

    @Id
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Set<CalendarEntryJpaEntity> entries;

    public CalendarJpaEntity(UUID id, Set<CalendarEntryJpaEntity> entries) {
        this.id = id;
        this.entries = entries;
    }

    public CalendarJpaEntity() {

    }

    public Calendar into() {
        var entries = this.entries
                .stream()
                .map(CalendarEntryJpaEntity::into)
                .collect(Collectors.toSet());

        return new Calendar.Builder(id)
                .withEntries(entries)
                .build();
    }

    public static CalendarJpaEntity from(Calendar calendar) {
        var entries = calendar.getEntries()
                .stream()
                .map(CalendarEntryJpaEntity::from)
                .collect(Collectors.toSet());

        return new CalendarJpaEntity(
                calendar.getId(),
                entries
        );
    }
}
