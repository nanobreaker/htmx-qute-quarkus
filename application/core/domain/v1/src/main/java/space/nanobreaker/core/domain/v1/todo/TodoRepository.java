package space.nanobreaker.core.domain.v1.todo;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

import java.util.List;

public interface TodoRepository {

    Uni<Result<Todo, Error>> save(Todo Todo);

    Uni<Result<Todo, Error>> findByTodoId(TodoId id);

    Uni<Result<List<Todo>, Error>> listTodos();

    Uni<Result<Void, Error>> deleteByTodoId(TodoId id);

}
