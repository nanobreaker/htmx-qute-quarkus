package dev.thatwhichis.jpa.adapter;

import dev.thatwhichis.library.error.Error;

public sealed interface JpaError extends Error {

    // @formatter:off
    record ThrowableError(Throwable throwable) implements JpaError { }
    record DeleteNotFound() implements JpaError { }
    record InconsistentDelete(Long actual, Long expected) implements JpaError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case ThrowableError t -> "jpa error: throwable error %s".formatted(t.throwable().getMessage());
            case DeleteNotFound _ -> "jpa error: entity to delete not found";
            case InconsistentDelete _ -> "jpa error: number of deleted entities does match with request";
        };
    }
}
