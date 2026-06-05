package dev.thatwhichis.framework.event;

import dev.thatwhichis.library.error.Error;

import java.util.Collection;

public sealed interface DispatchError extends Error {

    //@formatter:off
    record DomainEventsDispatchFailed(Collection<Error> errors) implements DispatchError { }
    record Uncategorized(Throwable throwable) implements DispatchError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case DomainEventsDispatchFailed e -> "Domain events dispatch failed [errors: %s]"
                    .formatted(e.errors().stream().map(Error::describe).toList());
            case Uncategorized t -> "Events dispatch failed [error: %s]".formatted(t.throwable().getMessage());
        };
    }
}
