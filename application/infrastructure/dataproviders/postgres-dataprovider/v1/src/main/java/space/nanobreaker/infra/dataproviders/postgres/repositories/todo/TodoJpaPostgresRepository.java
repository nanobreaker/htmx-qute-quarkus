package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.Command;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.ddd.Entity;
import space.nanobreaker.infra.dataproviders.postgres.repositories.JpaError;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class TodoJpaPostgresRepository
        implements TodoRepository, PanacheRepositoryBase<TodoJpaEntity, TodoJpaId> {

    @WithSpan
    @Override
    public Uni<Result<Todo, Error>> save(final Todo todo) {
        var jpaEntity = TodoJpaEntity.from(todo);

        return PanacheRepositoryBase.super
                .persistAndFlush(jpaEntity)
                .map(TodoJpaEntity::into)
                .map(Result::<Todo, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
    @Override
    public Uni<Result<Option<Todo>, Error>> find(final TodoId id) {
        var jpaId = TodoJpaId.from(id);

        return this.getSession()
                .flatMap(session -> session.find(TodoJpaEntity.class, jpaId))
                .map(Option::some)
                .map(jpaEntity -> jpaEntity.map(TodoJpaEntity::into))
                .map(Result::<Option<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
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
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
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
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
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
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
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
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
    @Override
    public Uni<Result<Void, Error>> update(
            final Set<Todo> todos,
            final Command.Todo.Update.Payload payload
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
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
    @Override
    public Uni<Result<Void, Error>> delete(final TodoId id) {
        var jpaId = TodoJpaId.from(id);

        return this.deleteById(jpaId)
                .map(result -> result
                        ? Result.<Void, Error>empty()
                        : Result.<Void, Error>err(new JpaError.DeleteNotFound())
                )
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
    @Override
    public Uni<Result<Void, Error>> delete(final Set<TodoId> ids) {
        var jpaIds = ids.stream().map(TodoJpaId::from).toList();
        var params = Parameters.with("ids", jpaIds);

        return this.delete("id in :ids", params)
                .map(count -> count == ids.size()
                        ? Result.<Void, Error>empty()
                        : Result.<Void, Error>err(new JpaError.IncosistentDelete(count, (long) ids.size()))
                )
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }

    @WithSpan
    @Override
    public Uni<Result<Void, Error>> deleteAll(final String username) {
        var params = Parameters.with("username", username);

        return this.delete("id.username = :username", params)
                .map(_ -> Result.<Void, Error>empty())
                .onFailure().recoverWithItem(t -> err(new JpaError.ThrowableError(t)));
    }
}
