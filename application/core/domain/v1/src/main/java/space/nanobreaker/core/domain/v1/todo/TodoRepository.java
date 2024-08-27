package space.nanobreaker.core.domain.v1.todo;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.Option;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

public interface TodoRepository {

    Uni<Todo> save(
            final Todo Todo
    );

    Uni<Option<Todo>> findById(
            final TodoId id
    );

    Uni<Stream<Todo>> list(
            final String username
    );

    Uni<Stream<Todo>> listBy(
            final String username,
            final Set<String> searchPatterns
    );

    Uni<Void> update(
            final Todo todo,
            final Option<String> someTitle,
            final Option<String> someDescription,
            final Option<LocalDateTime> someStart,
            final Option<LocalDateTime> someEnd
    );

}