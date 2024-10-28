package space.nanobreaker.core.usecases.v1.todo;

import space.nanobreaker.library.error.Error;

public sealed interface TodoError extends Error {

    // @formatter:off
    record NotFound() implements TodoError { }
    record Unknown() implements TodoError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case NotFound _ -> "todo not found";
            case Unknown _ -> "hack";
        };
    }
}
