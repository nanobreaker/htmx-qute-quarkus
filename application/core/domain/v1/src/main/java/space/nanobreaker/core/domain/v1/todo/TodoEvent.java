package space.nanobreaker.core.domain.v1.todo;

import space.nanobreaker.ddd.DomainEvent;

import java.util.Set;

public sealed interface TodoEvent extends DomainEvent {

    // @formatter:off
    record Created(Todo todo)       implements TodoEvent { }
    record Updated(Set<Todo> todos) implements TodoEvent { }
    record Deleted(Set<TodoId> ids) implements TodoEvent { }
    // @formatter:on

    @Override
    default String key() {
        return switch (this) {
            case Created _ -> "todo.created";
            case Updated _ -> "todo.updated";
            case Deleted _ -> "todo.deleted";
        };
    }
}
