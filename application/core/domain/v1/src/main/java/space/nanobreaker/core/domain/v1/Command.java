package space.nanobreaker.core.domain.v1;

import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.cqrs.CQRSEvent;
import space.nanobreaker.library.option.Option;

import java.time.ZonedDateTime;
import java.util.Set;

// @formatter:off
public sealed interface Command extends CQRSEvent {

    sealed interface Todo extends Command {
       record Create(
               String username,
               String title,
               Option<String> description,
               Option<ZonedDateTime> start,
               Option<ZonedDateTime> end
       ) implements Todo { }

       sealed interface Update extends Todo {
           record ByIds(
                   Set<TodoId> ids,
                   Payload payload
           ) implements Update { }

           record ByFilters(
                   String username,
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
                   Option<ZonedDateTime> start,
                   Option<ZonedDateTime> end
           ) { }
       }

       sealed interface Delete extends Todo {
           record All(String username)      implements Delete { }
           record ByIds(Set<TodoId> ids)    implements Delete { }
       }
    }
}
// @formatter:on