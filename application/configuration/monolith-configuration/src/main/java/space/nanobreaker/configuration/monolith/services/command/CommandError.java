package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.error.Error;

public sealed interface CommandError extends Error {

    // @formatter:off
    record CreationFailed(String description)   implements CommandError { }
    record DeletionFailed(String description)   implements CommandError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case CreationFailed e -> "failed to create todo: %s".formatted(e.description());
            case DeletionFailed e -> "failed to delete todo: %s".formatted(e.description());
        };
    }
}