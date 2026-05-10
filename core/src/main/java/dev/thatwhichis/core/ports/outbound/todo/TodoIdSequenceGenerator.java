package dev.thatwhichis.core.ports.outbound.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import io.smallrye.mutiny.Uni;

public interface TodoIdSequenceGenerator {

    Uni<Void> increment(String username);

    Uni<TodoId> next(String username);
}
