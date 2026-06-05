package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.core.ports.inbound.todo.TodoCommand;
import dev.thatwhichis.core.ports.outbound.todo.TodoRepository;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.error.JpaError;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class TodoJpaRepository
        implements TodoRepository, PanacheRepositoryBase<TodoJpaEntity, TodoJpaId> {

    @Override
    @WithSpan("save")
    public Uni<Result<Todo, Error>> save(final Todo todo) {
        var jpaEntity = TodoJpaEntity.from(todo);

        return PanacheRepositoryBase.super
                .persistAndFlush(jpaEntity)
                .map(TodoJpaEntity::into)
                .map(Result::<Todo, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    @WithSpan("find")
    public Uni<Result<Option<Todo>, Error>> find(final TodoId id) {
        var jpaId = TodoJpaId.from(id);

        return this.findById(jpaId)
                .map(Option::some)
                .map(jpaEntity -> jpaEntity.map(TodoJpaEntity::into))
                .map(Result::<Option<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Set<Todo>, Error>> list(final UUID userId) {
        var params = Parameters.with("userId", userId);
        var sorting = Sort.ascending("id.id");

        return this.find("id.userId = :userId", sorting, params)
                .list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(TodoJpaEntity::into)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Set<Todo>, Error>> list(
            final UUID userId,
            final Set<String> filters
    ) {
        var params = Parameters.with("userId", userId).and("filters", filters);
        var sorting = Sort.ascending("id.id");

        return this.find("id.userId = :userId and title in :filters", sorting, params)
                .list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(TodoJpaEntity::into)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Set<Todo>, Error>> list(final Set<TodoId> ids) {
        var jpaIds = ids.stream().map(TodoJpaId::from).toList();
        var params = Parameters.with("ids", jpaIds);
        var sorting = Sort.by("id.id");

        return this.find("id in :ids", sorting, params)
                .list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(TodoJpaEntity::into)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Set<Todo>, Error>> list(
            final Set<TodoId> ids,
            final Set<String> filters
    ) {
        var jpaIds = ids.stream().map(TodoJpaId::from).toList();
        var params = Parameters.with("ids", jpaIds).and("filters", filters);
        var sorting = Sort.by("id.id");

        return this.find("id in :ids and title in :filters", sorting, params)
                .list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(TodoJpaEntity::into)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Void, Error>> update(
            final Set<TodoId> ids,
            final TodoCommand.Update.Payload payload
    ) {
        var jpaIds = ids.stream().map(TodoJpaId::from).toList();
        var params = Parameters.with("ids", jpaIds);
        var sorting = Sort.by("id.id");

        return this
                .find("id in :ids", sorting, params)
                .list()
                .flatMap(jpaEntities -> {
                            var updates = jpaEntities
                                    .stream()
                                    .map(entry -> {
                                        payload.description().ifPresent(description -> entry.setDescription(description));
                                        payload.title().ifPresent(title -> entry.setTitle(title));
                                        payload.start().ifPresent(start -> entry.setStart(start));
                                        payload.end().ifPresent(end -> entry.setEnd(end));
                                        return this.flush().replaceWith(Result.<Void, Error>empty());
                                    })
                                    .toList();

                            return Uni
                                    .join()
                                    .all(updates)
                                    .andFailFast();
                        }
                )
                .map(_ -> Result.<Void, Error>empty())
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Void, Error>> delete(final TodoId id) {
        var jpaId = TodoJpaId.from(id);

        return this.deleteById(jpaId)
                .map(result -> result
                        ? Result.<Void, Error>empty()
                        : Result.<Void, Error>err(new JpaError.DeleteNotFound())
                )
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Void, Error>> delete(final Set<TodoId> ids) {
        var jpaIds = ids.stream().map(TodoJpaId::from).toList();
        var params = Parameters.with("ids", jpaIds);

        return this.delete("id in :ids", params)
                .map(count -> count == ids.size()
                        ? Result.<Void, Error>empty()
                        : Result.<Void, Error>err(new JpaError.InconsistentDelete(count, (long) ids.size()))
                )
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }

    @Override
    public Uni<Result<Void, Error>> deleteAll(final UUID userId) {
        var params = Parameters.with("userId", userId);

        return this.delete("id.userId = :userId", params)
                .map(_ -> Result.<Void, Error>empty())
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }
}
