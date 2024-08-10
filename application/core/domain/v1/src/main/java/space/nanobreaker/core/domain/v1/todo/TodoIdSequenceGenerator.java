package space.nanobreaker.core.domain.v1.todo;

import io.smallrye.mutiny.Uni;

public interface TodoIdSequenceGenerator {

    Uni<Void> increment(
            final String username
    );

    Uni<TodoId> next(
            final String username
    );

}
