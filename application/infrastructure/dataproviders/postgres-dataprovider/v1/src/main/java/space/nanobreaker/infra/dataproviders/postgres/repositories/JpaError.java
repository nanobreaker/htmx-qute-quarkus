package space.nanobreaker.infra.dataproviders.postgres.repositories;

import space.nanobreaker.library.error.Error;

public sealed interface JpaError extends Error {

    record ThrowableError(Throwable throwable)  implements JpaError { }

    @Override
    default String describe() {
        return switch (this) {
            case ThrowableError t ->
                    "jpa error: throwable error %s".formatted(t.throwable().getMessage());
        };
    }
}
