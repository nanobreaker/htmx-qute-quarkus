package dev.thatwhichis.core.ports.inbound.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.framework.cqrs.Command;
import dev.thatwhichis.library.option.Option;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

sealed public interface TodoCommand extends Command {

    //@formatter:off
    record Create(
            UUID userId,
            String title,
            Option<String> description,
            Option<Instant> start,
            Option<Instant> end
    ) implements TodoCommand { }

    sealed interface Update extends TodoCommand {

        record ByIds(
                Set<TodoId> ids,
                Payload payload
        ) implements Update { }

        record ByFilters(
                UUID userId,
                Set<String> filters,
                Payload payload
        ) implements Update { }

        record ByIdsAndFilters(
                Set<TodoId> ids,
                Set<String> filters,
                Payload payload
        ) implements Update { }

        record Payload(
                Option<String> title,
                Option<String> description,
                Option<Instant> start,
                Option<Instant> end
        ) { }
    }

    sealed interface Delete extends TodoCommand {

        record All(UUID userId) implements Delete { }

        record ByIds(Set<TodoId> ids) implements Delete { }
    }
    //@formatter:on
}