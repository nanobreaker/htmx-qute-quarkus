package dev.thatwhichis.core.ports.inbound.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.framework.cqrs.Query;

import java.util.Set;
import java.util.UUID;

sealed public interface TodoQuery extends Query {

    //@formatter:off
    sealed interface Get extends TodoQuery {

        record ById(TodoId id) implements Get { }
    }

    sealed interface List extends TodoQuery {

        record All(UUID userId) implements List { }

        record ByIds(Set<TodoId> ids) implements List { }

        record ByFilters(UUID userId, Set<String> filters) implements List { }

        record ByIdsAndFilters(Set<TodoId> ids, Set<String> filters) implements List { }
    }
    //@formatter:on
}

