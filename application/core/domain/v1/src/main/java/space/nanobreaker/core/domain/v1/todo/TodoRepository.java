package space.nanobreaker.core.domain.v1.todo;

import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.Command.Todo.Update.Payload;
import space.nanobreaker.jpa.Repository;
import space.nanobreaker.library.error.Error;

import java.util.Set;

public interface TodoRepository extends Repository {

    Uni<Result<Todo, Error>> save(Todo Todo);

    Uni<Result<Todo, Error>> get(TodoId id);

    Uni<Result<Set<Todo>, Error>> list(String username);

    Uni<Result<Set<Todo>, Error>> list(String username, Set<String> filters);

    Uni<Result<Set<Todo>, Error>> list(Set<TodoId> ids);

    Uni<Result<Set<Todo>, Error>> list(Set<TodoId> ids, Set<String> filters);

    Uni<Result<Void, Error>> update(Set<Todo> todos, Payload payload);

    Uni<Result<Void, Error>> delete(TodoId id);

    Uni<Result<Void, Error>> delete(Set<TodoId> ids);

    Uni<Result<Void, Error>> deleteAll(String username);
}