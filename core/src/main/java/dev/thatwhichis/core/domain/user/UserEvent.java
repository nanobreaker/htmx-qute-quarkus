package dev.thatwhichis.core.domain.user;

import dev.thatwhichis.framework.event.DomainEvent;

public sealed interface UserEvent extends DomainEvent {

    //@formatter:off
    record Created(User user) implements UserEvent { }
    //@formatter:on

    @Override
    default String key() {
        return switch (this) {
            case UserEvent.Created _ -> "user.created";
        };
    }
}
