package space.nanobreaker.ddd;

import java.util.Objects;

public abstract class AggregateRoot<Id> extends Entity<Id> {

    protected AggregateRoot(Id id) {
        super(Objects.requireNonNull(id));
    }
}