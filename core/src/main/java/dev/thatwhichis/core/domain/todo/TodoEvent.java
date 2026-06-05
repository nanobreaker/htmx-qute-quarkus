package dev.thatwhichis.core.domain.todo;

import dev.thatwhichis.framework.event.DomainEvent;

import java.util.UUID;

import static dev.thatwhichis.core.ports.inbound.todo.TodoCommand.Update.Payload;

public sealed interface TodoEvent extends DomainEvent {

    // @formatter:off
    record Created(Todo todo)                   implements TodoEvent { }
    record Updated(Todo todo, Payload payload)  implements TodoEvent { }
    record Deleted(TodoId todoId)               implements TodoEvent { }
    record DeletedAll(UUID userId)              implements TodoEvent { }
    // @formatter:on

    @Override
    default String key() {
        return switch (this) {
            case Created _ -> "todo.created";
            case Updated _ -> "todo.updated";
            case Deleted _ -> "todo.deleted";
            case DeletedAll _ -> "todo.deleted.all";
        };
    }
}
