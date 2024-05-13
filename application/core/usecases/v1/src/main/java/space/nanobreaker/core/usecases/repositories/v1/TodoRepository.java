package space.nanobreaker.core.usecases.repositories.v1;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.domain.v1.TodoId;

import java.util.List;
import java.util.UUID;

public interface TodoRepository {

    Uni<Todo> persist(Todo Todo);

    Uni<Todo> findByTodoId(TodoId id);

    Uni<List<Todo>> listAllTodos();

    Uni<Boolean> deleteByTodoId(TodoId id);

}
