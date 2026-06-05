package dev.thatwhichis.library.error;

public sealed interface JpaError extends Error {

    // @formatter:off
    record Uncategorized(Throwable throwable) implements JpaError { }
    record EntityNotFound() implements JpaError { }
    record DeleteNotFound() implements JpaError { }
    record InconsistentDelete(Long actual, Long expected) implements JpaError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case Uncategorized t -> "jpa error: throwable error %s".formatted(t.throwable().getMessage());
            case EntityNotFound _ -> "jpa error: entity not found";
            case DeleteNotFound _ -> "jpa error: entity to delete not found";
            case InconsistentDelete _ -> "jpa error: number of deleted entities does match with request";
        };
    }
}
