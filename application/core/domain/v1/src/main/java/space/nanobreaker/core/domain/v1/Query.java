package space.nanobreaker.core.domain.v1;

import space.nanobreaker.core.domain.v1.todo.TodoId;

import java.util.Set;

// @formatter:off
public sealed interface Query {

    sealed interface Todo extends Query {
        sealed interface Get extends Todo {
            record ById(TodoId id) implements Get { }
        }

        sealed interface List extends Todo {
            record All(String username)                                     implements List { }
            record ByIds(Set<TodoId> ids)                                   implements List { }
            record ByFilters(String username, Set<String> filters)          implements List { }
            record ByIdsAndFilters(Set<TodoId> ids, Set<String> filters)    implements List { }
        }
    }

    sealed interface Calendar   extends Query {
        record Show() implements Calendar { }
    }

    sealed interface User       extends Query {
        record Show() implements User { }
    }
}
// @formatter:on
