package space.nanobreaker.core.domain.v1.todo;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.jpa.Repository;
import space.nanobreaker.library.either.Either;
import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.result.Result;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface TodoRepository extends Repository {

    Uni<Result<Todo, Error>> save(Todo Todo);

    Uni<Result<Todo, Error>> find(TodoId id);

    Uni<Result<Set<Todo>, Error>> list(String username);

    Uni<Result<Set<Todo>, Error>> list(Set<TodoId> ids);

    Uni<Result<Set<Todo>, Error>> list(
            Either<String, Set<TodoId>> usernameOrIds,
            Option<List<String>> filters
    );

    Uni<Result<Void, Error>> update(
            Todo todo,
            Option<String> someTitle,
            Option<String> someDescription,
            Option<ZonedDateTime> someStart,
            Option<ZonedDateTime> someEnd
    );

    Uni<Result<Void, Error>> update(
            Set<Todo> todos,
            Option<String> someTitle,
            Option<String> someDescription,
            Option<ZonedDateTime> someStart,
            Option<ZonedDateTime> someEnd
    );

    Uni<Result<Void, Error>> delete(TodoId id);

    Uni<Result<Void, Error>> delete(Set<TodoId> ids);
}