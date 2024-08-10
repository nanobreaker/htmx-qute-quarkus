package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoIdSequenceGenerator;

public class TodoIdPostgresSequenceGenerator
        implements TodoIdSequenceGenerator, PanacheRepositoryBase<TodoJpaIdSequence, String> {

    @WithTransaction
    public Uni<Void> increment(
            final String username
    ) {
        final String query = "seq = (seq + 1) where username = :username";
        final Parameters parameters = Parameters.with("username", username);

        return this.update(query, parameters)
                .chain(this::flush);
    }

    @WithTransaction
    public Uni<TodoId> next(
            final String username
    ) {
        return this.findById(username)
                .onItem().ifNull().switchTo(() -> this.create(username))
                .map(this::mapSequenceToId);
    }

    private Uni<TodoJpaIdSequence> create(
            final String username
    ) {
        final TodoJpaIdSequence idSequence = new TodoJpaIdSequence();
        idSequence.setUsername(username);
        idSequence.setSeq(0);

        return this.persistAndFlush(idSequence);
    }

    private TodoId mapSequenceToId(
            final TodoJpaIdSequence idSequence
    ) {
        return new TodoId(idSequence.getSeq(), idSequence.getUsername());
    }

}
