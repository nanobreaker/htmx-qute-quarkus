package space.nanobreaker.core.domain.v1.todo;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.Option;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

public interface TodoRepository {

    Uni<Todo> save(
            Todo Todo
    );

    Uni<Option<Todo>> findById(
            TodoId id
    );

    Uni<Stream<Todo>> list(
            String username
    );

    Uni<Stream<Todo>> listBy(
            String username,
            Set<String> searchPatterns
    );

    Uni<Void> update(
            Todo todo,
            Option<String> someTitle,
            Option<String> someDescription,
            Option<LocalDateTime> someStart,
            Option<LocalDateTime> someEnd
    );

    Uni<Void> deleteByTodoId(
            TodoId id
    );

}