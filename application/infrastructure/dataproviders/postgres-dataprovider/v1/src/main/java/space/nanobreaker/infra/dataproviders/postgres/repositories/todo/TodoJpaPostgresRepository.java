package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Option;
import space.nanobreaker.library.Result;

import java.util.List;
import java.util.Optional;

public class TodoJpaPostgresRepository
        implements TodoRepository, PanacheRepositoryBase<TodoJpaEntity, TodoJpaId> {

    @Override
    public Result<Todo, Error> save(final Todo todo) {
        final TodoJpaEntity jpaEntity = mapToJpaEntity(todo);

        PanacheRepositoryBase.super.persist(jpaEntity);

        return Result.ok(null);
    }

    @Override
    public Result<Option<Todo>, Error> findByTodoId(TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        final TodoJpaEntity todoJpaEntity = findById(jpaId);
        final Todo todo = mapToDomainEntity(todoJpaEntity);

        return Result.ok(Option.over(Optional.of(todo)));
    }

    @Override
    public Result<List<Todo>, Error> listTodos() {
        final List<Todo> all = this.listAll(Sort.ascending("end"))
                .stream()
                .map(this::mapToDomainEntity)
                .toList();

        return Result.ok(all);
    }

    @Override
    public Result<Void, Error> deleteByTodoId(TodoId id) {
        final TodoJpaId jpaId = mapToJpaId(id);

        final boolean isDeleted = this.deleteById(jpaId);

        if (isDeleted == false)
            Result.err(new Error() {
                @Override
                public int hashCode() {
                    return super.hashCode();
                }
            });

        return Result.ok(null);
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