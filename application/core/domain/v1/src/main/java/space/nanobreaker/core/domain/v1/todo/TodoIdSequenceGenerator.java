package space.nanobreaker.core.domain.v1.todo;

import io.smallrye.mutiny.Uni;

public interface TodoIdSequenceGenerator {

    Uni<Void> increment(String username);

    Uni<TodoId> next(String username);
}
