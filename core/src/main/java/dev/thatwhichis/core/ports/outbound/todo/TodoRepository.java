package dev.thatwhichis.core.ports.outbound.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.core.ports.inbound.TodoCommand;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

import java.util.Set;

public interface TodoRepository {

    Uni<Result<Todo, Error>> save(Todo Todo);

    Uni<Result<Option<Todo>, Error>> find(TodoId id);

    Uni<Result<Set<Todo>, Error>> list(String username);

    Uni<Result<Set<Todo>, Error>> list(String username, Set<String> filters);

    Uni<Result<Set<Todo>, Error>> list(Set<TodoId> ids);

    Uni<Result<Set<Todo>, Error>> list(Set<TodoId> ids, Set<String> filters);

    Uni<Result<Void, Error>> update(Set<Todo> todos, TodoCommand.Update.Payload payload);

    Uni<Result<Void, Error>> delete(TodoId id);

    Uni<Result<Void, Error>> delete(Set<TodoId> ids);

    Uni<Result<Void, Error>> deleteAll(String username);
}