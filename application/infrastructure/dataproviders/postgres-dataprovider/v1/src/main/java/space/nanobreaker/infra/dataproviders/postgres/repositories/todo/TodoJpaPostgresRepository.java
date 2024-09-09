package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.library.None;
import space.nanobreaker.library.Option;
import space.nanobreaker.library.Some;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class TodoJpaPostgresRepository
        implements TodoRepository, PanacheRepositoryBase<TodoJpaEntity, TodoJpaId> {

    @Override
    @WithSpan("saveTodo")
    public Uni<Todo> save(final Todo todo) {
        final TodoJpaEntity jpaEntity = mapToJpaEntity(todo);

        return PanacheRepositoryBase.super.persistAndFlush(jpaEntity)
                .map(this::mapToDomainEntity);
    }

    @Override
    @WithSpan("findByTodoId")
    public Uni<Option<Todo>> findById(final TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        return this.getSession()
                .flatMap(session ->
                        session.find(TodoJpaEntity.class, jpaId)
                )
                .map(Option::of)
                .map(todoOption ->
                        switch (todoOption) {
                            case Some(TodoJpaEntity e) -> Option.of(mapToDomainEntity(e));
                            case None() -> Option.none();
                        }
                );
    }

    @Override
    public Uni<Stream<Todo>> list(
            final String username
    ) {
        final String query = """
                select t from TodoJpaEntity t
                where
                    t.id.username = :username
                order by
                    t.id.id
                """;

        return this.getSession()
                .flatMap(session ->
                        session.createQuery(query, TodoJpaEntity.class)
                                .setParameter("username", username)
                                .getResultList()
                )
                .map(jpaEntities ->
                        jpaEntities.stream()
                                .map(this::mapToDomainEntity)
                );
    }

    @Override
    public Uni<Stream<Todo>> listBy(Set<TodoId> ids) {
        final List<TodoJpaId> jpaIds = ids.stream()
                .map(this::mapToJpaId)
                .toList();
        final Parameters params = Parameters.with("ids", jpaIds);

        return this.find("id in :ids", params)
                .list()
                .map(jpaEntities ->
                        jpaEntities.stream()
                                .map(this::mapToDomainEntity)
                );
    }

    @Override
    public Uni<Stream<Todo>> listBy(
            final String username,
            final Option<Set<TodoId>> idsOption,
            final Option<List<String>> filtersOption
    ) {
        record Box(Option<Set<TodoId>> ids, Option<List<String>> filters) {
        }
        final Box box = new Box(idsOption, filtersOption);
        final PanacheQuery<TodoJpaEntity> query = switch (box) {
            case Box(
                    Some(final Set<TodoId> ids),
                    Some(final List<String> filters)
            ) -> {
                final List<TodoJpaId> jpaIds = ids.stream()
                        .map(this::mapToJpaId)
                        .toList();
                final Parameters params = Parameters.with("ids", jpaIds)
                        .and("filters", filters);

                yield this.find("id in :ids and title in :filters", params);
            }
            case Box(
                    Some(final Set<TodoId> ids),
                    None<List<String>> ignored
            ) -> {
                final List<TodoJpaId> jpaIds = ids.stream()
                        .map(this::mapToJpaId)
                        .toList();
                final Parameters params = Parameters.with("ids", jpaIds);

                yield this.find("id in :ids", params);
            }
            case Box(
                    None<Set<TodoId>> ignored,
                    Some(final List<String> filters)
            ) -> {
                final Parameters params = Parameters.with("username", username)
                        .and("filters", filters);
                yield this.find("id.username = :username and title in :filters", params);
            }
            default -> {
                final Parameters params = Parameters.with("username", username);
                yield this.find("id.username = :username and title in :filters", params);
            }
        };

        return query.list()
                .map(jpaEntities ->
                        jpaEntities.stream()
                                .map(this::mapToDomainEntity)
                );
    }

    @Override
    @WithSpan("updateById")
    @WithTransaction
    public Uni<Void> update(
            final Todo todo,
            final Option<String> someTitle,
            final Option<String> someDescription,
            final Option<LocalDateTime> someStart,
            final Option<LocalDateTime> someEnd
    ) {
        final TodoJpaId id = mapToJpaId(todo.getId());
        final Parameters parameters = Parameters.with("id", id);
        final List<String> fields = new ArrayList<>();

        if (someTitle instanceof Some(final String title)) {
            parameters.and("title", title);
            fields.add("title = :title");
        }
        if (someDescription instanceof Some(final String description)) {
            parameters.and("description", description);
            fields.add("description = :description");
        }
        if (someStart instanceof Some(final LocalDateTime start)) {
            parameters.and("start", start);
            fields.add("startDateTime = :start");
        }
        if (someEnd instanceof Some(final LocalDateTime end)) {
            parameters.and("end", end);
            fields.add("endDateTime = :end");
        }

        final String fieldsJoined = String.join(",", fields);
        final String query = fieldsJoined + " where id = :id";

        return this.update(query, parameters)
                .chain(this::flush);
    }

    @Override
    public Uni<Void> deleteByTodoId(final TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        return this.deleteById(jpaId)
                .replaceWithVoid();
    }

    @Override
    public Uni<Void> deleteByTodoIds(Set<TodoId> ids) {
        return Uni.createFrom().voidItem();
    }

    protected TodoJpaId mapToJpaId(final TodoId id) {
        final TodoJpaId todoJpaId = new TodoJpaId();
        todoJpaId.setId(id.getId());
        todoJpaId.setUsername(id.getUsername());
        return todoJpaId;
    }

    protected TodoJpaEntity mapToJpaEntity(final Todo todo) {
        final TodoJpaId id = mapToJpaId(todo.getId());
        return new TodoJpaEntity(
                id,
                todo.getTitle(),
                todo.getDescription().orElse(null),
                todo.getState(),
                todo.getStart().orElse(null),
                todo.getEnd().orElse(null)
        );
    }

    protected TodoId mapToDomainId(final TodoJpaId id) {
        return new TodoId(
                id.getId(),
                id.getUsername()
        );
    }

    protected Todo mapToDomainEntity(final TodoJpaEntity jpaEntity) {
        final TodoId id = mapToDomainId(jpaEntity.getId());
        return new Todo(
                id,
                jpaEntity.getTitle(),
                jpaEntity.getDescription(),
                jpaEntity.getState(),
                jpaEntity.getStartDateTime(),
                jpaEntity.getEndDateTime()
        );
    }

}