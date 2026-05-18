package dev.thatwhichis.core.ports.inbound.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.framework.cqrs.Command;
import dev.thatwhichis.library.option.Option;

import java.time.ZonedDateTime;
import java.util.Set;

sealed public interface TodoCommand extends Command {

    record Create(
            String username,
            String title,
            Option<String> description,
            Option<ZonedDateTime> start,
            Option<ZonedDateTime> end
    ) implements TodoCommand {

    }

    sealed interface Update extends TodoCommand {

        record ByIds(
                Set<TodoId> ids,
                Payload payload
        ) implements Update {

        }

        record ByFilters(
                String username,
                Set<String> filters,
                Payload payload
        ) implements Update {

        }

        record ByIdsAndFilters(
                Set<TodoId> ids,
                Set<String> filters,
                Payload payload
        ) implements Update {

        }

        record Payload(
                Option<String> title,
                Option<String> description,
                Option<ZonedDateTime> start,
                Option<ZonedDateTime> end
        ) {

        }
    }

    sealed interface Delete extends TodoCommand {

        record All(String username) implements Delete {

        }

        record ByIds(Set<TodoId> ids) implements Delete {

        }
    }
}