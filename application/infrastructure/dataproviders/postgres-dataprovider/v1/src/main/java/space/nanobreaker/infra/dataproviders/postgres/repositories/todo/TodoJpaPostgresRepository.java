package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.ddd.Entity;
import space.nanobreaker.infra.dataproviders.postgres.repositories.JpaError;
import space.nanobreaker.library.either.Either;
import space.nanobreaker.library.either.Left;
import space.nanobreaker.library.either.Right;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.error.None;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.option.Some;
import space.nanobreaker.library.result.Result;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TodoJpaPostgresRepository
        implements TodoRepository, PanacheRepositoryBase<TodoJpaEntity, TodoJpaId> {

    @WithSpan("saveTodo")
    @Override
    public Uni<Result<Todo, Error>> save(final Todo todo) {
        final TodoJpaEntity jpaEntity = mapToJpaEntity(todo);

        return PanacheRepositoryBase.super
                .persistAndFlush(jpaEntity)
                .map(this::mapToDomainEntity)
                .map(Result::<Todo, Error>ok)
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @WithSpan("findByTodoId")
    @Override
    public Uni<Result<Todo, Error>> find(final TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        return this.getSession()
                .flatMap(session -> session.find(TodoJpaEntity.class, jpaId))
                .map(todoJpaEntity -> {
                    if (todoJpaEntity == null) {
                        return Result.<Todo, Error>err(new None());
                    } else {
                        var todo = this.mapToDomainEntity(todoJpaEntity);
                        return Result.<Todo, Error>ok(todo);
                    }
                })
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @WithSpan("listTodosByUsername")
    @Override
    public Uni<Result<Set<Todo>, Error>> list(final String username) {
        final var parameters = Parameters.with("username", username);

        return this.find("id.username = :username", Sort.ascending("id.id"), parameters)
                .list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(this::mapToDomainEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @WithSpan("listTodosByUsernameAndFilters")
    public Uni<Result<Set<Todo>, Error>> list(
            final String username,
            final Option<List<String>> filterOpt
    ) {
        final var parameters = Parameters.with("username", username);
        final var query = switch (filterOpt) {
            case Some(List<String> filters) -> {
                parameters.and("filters", filters);
                yield this.find("id.username = :username and title in :filters", parameters);
            }
            case space.nanobreaker.library.option.None() -> {
                yield this.find("id.username = :username", parameters);
            }
        };

        return query.list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(this::mapToDomainEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @WithSpan("listTodosByIds")
    @Override
    public Uni<Result<Set<Todo>, Error>> list(final Set<TodoId> ids) {
        final var jpaIds = ids.stream().map(this::mapToJpaId).toList();
        final var params = Parameters.with("idsOrUsername", jpaIds);

        return this.find("id in :idsOrUsername", Sort.by("id.id"), params)
                .list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(this::mapToDomainEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @WithSpan("listTodosByIdsAndFilter")
    public Uni<Result<Set<Todo>, Error>> list(
            final Set<TodoId> ids,
            final Option<List<String>> filterOpt
    ) {
        final var jpaIds = ids.stream().map(this::mapToJpaId).toList();
        final var params = Parameters.with("idsOrUsername", jpaIds);
        final var query = switch (filterOpt) {
            case Some(List<String> filters) -> {
                params.and("filters", filters);
                yield this.find("id in :idsOrUsername and title in :filters", Sort.by("id.id"), params);
            }
            case space.nanobreaker.library.option.None() -> {
                yield this.find("id in :idsOrUsername", Sort.by("id.id"), params);
            }
        };

        return query.list()
                .map(jpaEntities -> jpaEntities
                        .stream()
                        .map(this::mapToDomainEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                )
                .map(Result::<Set<Todo>, Error>ok)
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @WithSpan("listTodosByEitherUsernameOrIdsAndFilters")
    @Override
    public Uni<Result<Set<Todo>, Error>> list(
            final Either<String, Set<TodoId>> usernameOrIds,
            final Option<List<String>> filtersOption
    ) {
        return switch (usernameOrIds) {
            case Left(String username) -> this.list(username, filtersOption);
            case Right(Set<TodoId> ids) -> this.list(ids, filtersOption);
        };
    }

    @Override
    @WithSpan("updateTodos")
    public Uni<Result<Void, Error>> update(
            final Set<Todo> todos,
            final Option<String> someTitle,
            final Option<String> someDescription,
            final Option<ZonedDateTime> someStart,
            final Option<ZonedDateTime> someEnd
    ) {
        final Set<TodoJpaId> jpaIds = todos
                .stream()
                .map(Entity::getId)
                .map(this::mapToJpaId)
                .collect(Collectors.toUnmodifiableSet());

        final Parameters parameters = Parameters.with("idsOrUsername", jpaIds);
        final List<String> fields = new ArrayList<>();

        if (someTitle instanceof Some(final String title)) {
            parameters.and("title", title);
            fields.add("title = :title");
        }
        if (someDescription instanceof Some(final String description)) {
            parameters.and("description", description);
            fields.add("description = :description");
        }
        if (someStart instanceof Some(final ZonedDateTime start)) {
            parameters.and("start", start.toInstant());
            fields.add("startDateTime = :start");
        }
        if (someEnd instanceof Some(final ZonedDateTime end)) {
            parameters.and("end", end.toInstant());
            fields.add("endDateTime = :end");
        }

        final String fieldsJoined = String.join(",", fields);
        final String query = "%s where id in :idsOrUsername".formatted(fieldsJoined);

        return Panache.withTransaction(
                        () -> this.update(query, parameters)
                                .chain(this::flush)
                                .replaceWith(Result.<Error>okVoid())
                )
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @Override
    public Uni<Result<Void, Error>> delete(final TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        return this.deleteById(jpaId)
                .map(result -> {
                    if (!result) {
                        return Result.<Void, Error>err(new None());
                    } else {
                        return Result.<Error>okVoid();
                    }
                })
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    @Override
    public Uni<Result<Void, Error>> delete(final Set<TodoId> ids) {
        final Set<TodoJpaId> jpaIds = ids
                .stream()
                .map(this::mapToJpaId)
                .collect(Collectors.toUnmodifiableSet());
        final Parameters parameters = Parameters.with("idsOrUsername", jpaIds);

        return this.delete("where id in :idsOrUsername", parameters)
                .map(count -> {
                    if (count != ids.size()) {
                        return Result.<Void, Error>err(new None());
                    } else {
                        return Result.<Error>okVoid();
                    }
                })
                .onFailure().recoverWithItem(t -> Result.err(new JpaError.ThrowableError(t)));
    }

    private TodoJpaId mapToJpaId(final TodoId id) {
        final TodoJpaId todoJpaId = new TodoJpaId();
        todoJpaId.setId(id.getId());
        todoJpaId.setUsername(id.getUsername());
        return todoJpaId;
    }

    private TodoJpaEntity mapToJpaEntity(final Todo todo) {
        final TodoJpaId id = mapToJpaId(todo.getId());
        return new TodoJpaEntity(
                id,
                todo.getTitle(),
                todo.getDescription().orElse(null),
                todo.getStart().orElse(null),
                todo.getEnd().orElse(null)
        );
    }

    private TodoId mapToDomainId(final TodoJpaId id) {
        return new TodoId(id.getId(), id.getUsername());
    }

    private Todo mapToDomainEntity(final TodoJpaEntity jpaEntity) {
        final TodoId id = this.mapToDomainId(jpaEntity.getId());
        return new Todo(
                id,
                jpaEntity.getTitle(),
                jpaEntity.getDescription(),
                jpaEntity.getStartDateTime(),
                jpaEntity.getEndDateTime()
        );
    }
}
