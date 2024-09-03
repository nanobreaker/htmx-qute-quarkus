package space.nanobreaker.core.usecases.v1.todo;

import space.nanobreaker.library.Error;

public sealed interface TodoErr extends Error {

    // @formatter:off
    record ArgumentOrOptionRequired() implements TodoErr { }
    // @formatter:on
}
