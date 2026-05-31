package dev.thatwhichis.core.domain.calendar;

import dev.thatwhichis.framework.ddd.Entity;

import java.util.Set;
import java.util.UUID;

public class Calendar extends Entity<UUID> {

    private Set<CalendarEntry> entries;

    protected Calendar(UUID id, Set<CalendarEntry> entries) {
        super(id);
        this.entries = entries;
    }

    public Set<CalendarEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<CalendarEntry> entries) {
        this.entries = entries;
    }

    public static final class Builder {

        private final UUID id;
        private Set<CalendarEntry> entries;

        public Builder(UUID id) {
            this.id = id;
        }

        public Builder withEntries(Set<CalendarEntry> entries) {
            this.entries = entries;
            return this;
        }

        public Calendar build() {
            return new Calendar(id, entries);
        }
    }
}
