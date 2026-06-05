package dev.thatwhichis.framework.entity;

public abstract class Entity<Id> {

    private final Id id;

    protected Entity(Id id) {
        this.id = id;
    }

    public Id getId() {
        return id;
    }
}
