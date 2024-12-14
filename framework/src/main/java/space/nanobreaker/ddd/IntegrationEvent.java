package space.nanobreaker.ddd;

import space.nanobreaker.cqrs.Event;

public interface IntegrationEvent extends Event {

    String key();
}
