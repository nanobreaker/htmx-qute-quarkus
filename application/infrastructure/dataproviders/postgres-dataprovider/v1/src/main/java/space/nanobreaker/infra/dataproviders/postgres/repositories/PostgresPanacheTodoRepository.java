package space.nanobreaker.infra.dataproviders.postgres.repositories;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.domain.v1.TodoId;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;

import java.util.List;
import java.util.UUID;

public class PostgresPanacheTodoRepository
        implements TodoRepository, PanacheRepositoryBase<Todo, TodoId> {

    @Override
    public Uni<Todo> persist(Todo Todo) {
        return PanacheRepositoryBase.super.persist(Todo);
    }

    @Override
    public Uni<Todo> findByTodoId(TodoId id) {
        return findById(id);
    }

    @Override
    public Uni<List<Todo>> listAllTodos() {
        return this.listAll(Sort.ascending("end"));
    }

    @Override
    public Uni<Boolean> deleteByTodoId(TodoId id) {
        return this.deleteById(id);
    }


}
