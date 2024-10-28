package space.nanobreaker.ddd;

import java.util.ArrayList;
import java.util.Objects;
import java.util.SequencedCollection;

public abstract class AggregateRoot<Id> extends Entity<Id> {

    private final SequencedCollection<Event> domainEvents = new ArrayList<>();

    protected AggregateRoot(Id id) {
        super(Objects.requireNonNull(id));
    }

    protected void registerEvent(Event event) {
        domainEvents.add(event);
    }

    public SequencedCollection<Event> getDomainEvents() {
        return domainEvents;
    }
}