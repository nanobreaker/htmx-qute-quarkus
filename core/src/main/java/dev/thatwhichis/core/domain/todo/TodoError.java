package dev.thatwhichis.core.domain.todo;

import dev.thatwhichis.library.error.Error;

public sealed interface TodoError extends Error {

    // @formatter:off
    record NotFound() implements TodoError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case NotFound _ -> "todo not found";
        };
    }
}
