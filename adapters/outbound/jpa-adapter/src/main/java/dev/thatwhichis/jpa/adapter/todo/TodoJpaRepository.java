package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.core.ports.inbound.todo.TodoCommand;
import dev.thatwhichis.core.ports.outbound.todo.TodoRepository;
import dev.thatwhichis.framework.ddd.Entity;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.error.JpaError;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
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
    public Uni<Result<Set<Todo>, Error>> list(final String username) {
        var params = Parameters.with("username", username);
        var sorting = Sort.ascending("id.id");

        return this.find("id.username = :username", sorting, params)
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
            final String username,
            final Set<String> filters
    ) {
        var params = Parameters.with("username", username).and("filters", filters);
        var sorting = Sort.ascending("id.id");

        return this.find("id.username = :username and title in :filters", sorting, params)
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
            final Set<Todo> todos,
            final TodoCommand.Update.Payload payload
    ) {
        var jpaIds = todos.stream().map(Entity::getId).map(TodoJpaId::from).toList();
        var parameters = Parameters.with("ids", jpaIds);
        var fields = new ArrayList<String>();

        payload.title().ifPresent(title -> {
            parameters.and("title", title);
            fields.add("title = :title");
        });
        payload.description().ifPresent(description -> {
            parameters.and("description", description);
            fields.add("description = :description");
        });
        payload.start().ifPresent(start -> {
            parameters.and("start", start.toInstant());
            fields.add("startDateTime = :start");
        });
        payload.end().ifPresent(end -> {
            parameters.and("end", end.toInstant());
            fields.add("endDateTime = :end");
        });

        var fieldsJoined = String.join(",", fields);
        var query = "%s where id in :ids".formatted(fieldsJoined);

        return Panache.withTransaction(
                        () -> this.update(query, parameters)
                                .chain(this::flush)
                                .replaceWith(Result.<Void, Error>empty())
                )
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
    public Uni<Result<Void, Error>> deleteAll(final String username) {
        var params = Parameters.with("username", username);

        return this.delete("id.username = :username", params)
                .map(_ -> Result.<Void, Error>empty())
                .onFailure().recoverWithItem(t -> err(new JpaError.Uncategorized(t)));
    }
}
