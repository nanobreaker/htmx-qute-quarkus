package dev.thatwhichis.core.domain.todo;

import dev.thatwhichis.framework.ddd.DomainEvent;

public sealed interface TodoEvent extends DomainEvent {

    // @formatter:off
    record Created(Todo todo)       implements TodoEvent { }
    record Updated(Todo todo)       implements TodoEvent { }
    record Deleted(TodoId todoId)   implements TodoEvent { }
    record DeletedAll()             implements TodoEvent { }
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
