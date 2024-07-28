package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.*;

import java.util.List;
import java.util.Optional;

public class TodoJpaPostgresRepository
        implements TodoRepository, PanacheRepositoryBase<TodoJpaEntity, TodoJpaId> {

    @Override
    @WithSpan("saveTodo")
    public Uni<Result<Todo, Error>> save(final Todo todo) {
        final TodoJpaEntity jpaEntity = mapToJpaEntity(todo);

        return PanacheRepositoryBase.super.persist(jpaEntity)
                .map(todoJpaEntity -> Result.ok(mapToDomainEntity(todoJpaEntity)));
    }

    @Override
    @WithSpan("findByTodoId")
    public Uni<Result<Todo, Error>> findByTodoId(TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        return findById(jpaId)
                .map(todoJpaEntity -> Option.over(Optional.of(todoJpaEntity)))
                .map(optionTodoJpaEntity -> switch (optionTodoJpaEntity) {
                    case Some(TodoJpaEntity entity) -> Result.ok(mapToDomainEntity(entity));
                    case None() -> Result.err(null);
                });
    }

    @Override
    @WithSpan("listTodos")
    public Uni<Result<List<Todo>, Error>> listTodos() {
        return this.listAll(Sort.ascending("end"))
                .map(jpaEntities -> jpaEntities.stream()
                        .map(this::mapToDomainEntity)
                        .toList()
                )
                .map(Result::ok);
    }

    @Override
    @WithSpan("deleteByTodoId")
    public Uni<Result<Void, Error>> deleteByTodoId(TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        return this.deleteById(jpaId)
                .map(isDeleted -> isDeleted ? Result.ok(null) : Result.err(null));
    }

    protected TodoJpaId mapToJpaId(final TodoId id) {
        return new TodoJpaId(
                id.getId(),
                id.getUsername()
        );
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