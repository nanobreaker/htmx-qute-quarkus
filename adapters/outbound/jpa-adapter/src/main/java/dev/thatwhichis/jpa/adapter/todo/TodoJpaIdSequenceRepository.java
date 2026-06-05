package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.core.ports.outbound.todo.TodoIdSequenceRepository;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.error.JpaError;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class TodoJpaIdSequenceRepository
        implements TodoIdSequenceRepository, PanacheRepositoryBase<TodoJpaIdSequence, UUID> {

    @WithSpan("next")
    @Override
    public Uni<TodoId> next(UUID userId) {
        return this.findById(userId)
                .onItem().ifNull().switchTo(() -> this.create(userId))
                .invoke(sequence -> sequence.setSeq(sequence.getSeq() + 1))
                .chain(sequence -> this.flush().replaceWith(sequence.into()));
    }

    @WithSpan("get")
    @Override
    public Uni<Result<TodoId, Error>> get(UUID userId) {
        return this.findById(userId)
                .map(TodoJpaIdSequence::into)
                .map(Result::<TodoId, Error>ok)
                .onFailure().recoverWithItem(throwable -> switch (throwable) {
                    case NullPointerException _ -> err(new JpaError.EntityNotFound());
                    default -> err(new JpaError.Uncategorized(throwable));
                });
    }

    private Uni<TodoJpaIdSequence> create(final UUID userId) {
        final TodoJpaIdSequence idSequence = new TodoJpaIdSequence();
        idSequence.setUserId(userId);
        idSequence.setSeq(0);

        return this.persistAndFlush(idSequence);
    }
}
