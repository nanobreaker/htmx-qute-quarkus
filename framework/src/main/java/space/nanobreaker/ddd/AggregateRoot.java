package space.nanobreaker.ddd;

import java.util.ArrayList;
import java.util.Objects;
import java.util.SequencedCollection;

public abstract class AggregateRoot<Id> extends Entity<Id> {

    private final SequencedCollection<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(Id id) {
        super(Objects.requireNonNull(id));
    }

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public SequencedCollection<DomainEvent> getDomainEvents() {
        return domainEvents;
    }
}