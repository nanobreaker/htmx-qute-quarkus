package space.nanobreaker.ddd;

import space.nanobreaker.cqrs.Event;

public interface DomainEvent extends Event {

    String key();
}
