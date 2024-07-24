package space.nanobreaker.core.domain.v1.todo;

import space.nanobreaker.library.Error;
import space.nanobreaker.library.Option;
import space.nanobreaker.library.Result;

import java.util.List;

public interface TodoRepository {

    Result<Todo, Error> save(Todo Todo);

    Result<Option<Todo>, Error> findByTodoId(TodoId id);

    Result<List<Todo>, Error> listTodos();

    Result<Void, Error> deleteByTodoId(TodoId id);

}
