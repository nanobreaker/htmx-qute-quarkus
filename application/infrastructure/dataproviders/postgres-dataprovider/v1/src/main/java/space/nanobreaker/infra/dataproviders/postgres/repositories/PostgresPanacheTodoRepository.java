package space.nanobreaker.infra.dataproviders.postgres.repositories;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;

import java.util.List;
import java.util.UUID;

public class PostgresPanacheTodoRepository implements TodoRepository, PanacheRepositoryBase<Todo, UUID> {

    @Override
    public Uni<Todo> persist(Todo Todo) {
        return PanacheRepositoryBase.super.persist(Todo);
    }

    @Override
    public Uni<Integer> complete(UUID id) {
        return this.update("completed = TRUE WHERE id = ?1", id);
    }

    @Override
    public Uni<Todo> findByTodoId(UUID id) {
        return findById(id);
    }

    @Override
    public Uni<List<Todo>> listAllTodos() {
        return this.listAll(Sort.ascending("target"));
    }

    @Override
    public Uni<Boolean> deleteByTodoId(UUID id) {
        return this.deleteById(id);
    }


}
