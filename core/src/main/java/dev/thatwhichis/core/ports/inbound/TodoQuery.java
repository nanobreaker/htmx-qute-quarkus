package dev.thatwhichis.core.ports.inbound;

import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.framework.cqrs.Query;

import java.util.Set;

sealed public interface TodoQuery extends Query {

    sealed interface Get extends TodoQuery {

        record ById(TodoId id) implements Get {

        }
    }

    sealed interface List extends TodoQuery {

        record All(String username) implements List {

        }

        record ByIds(Set<TodoId> ids) implements List {

        }

        record ByFilters(String username, Set<String> filters) implements List {

        }

        record ByIdsAndFilters(Set<TodoId> ids, Set<String> filters) implements List {

        }
    }
}

