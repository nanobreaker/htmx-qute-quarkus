package space.nanobreaker.infra.dataproviders.inmemory.repositories;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTodoRepository implements TodoRepository {

    Map<UUID, Todo> todoEntityConcurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public Uni<Todo> persist(Todo Todo) {
        return Uni.createFrom()
                .item(() -> todoEntityConcurrentHashMap.put(UUID.randomUUID(), Todo));
    }

    @Override
    public Uni<Integer> complete(UUID id) {
        return null;
    }

    @Override
    public Uni<Todo> findByTodoId(UUID id) {
        return Uni.createFrom()
                .item(() -> todoEntityConcurrentHashMap.get(id));
    }

    @Override
    public Uni<List<Todo>> listAllTodos() {
        return Uni.createFrom()
                .item(() -> todoEntityConcurrentHashMap.values().stream().toList());
    }

    @Override
    public Uni<Boolean> deleteByTodoId(UUID id) {
        return null;
    }
}
