package dev.thatwhichis.core.ports.outbound.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface TodoIdSequenceRepository {

    Uni<TodoId> next(UUID userId);

    Uni<Result<TodoId, Error>> get(UUID userId);
}
