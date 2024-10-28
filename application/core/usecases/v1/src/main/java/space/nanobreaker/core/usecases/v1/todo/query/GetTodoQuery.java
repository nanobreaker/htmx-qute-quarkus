package space.nanobreaker.core.usecases.v1.todo.query;

import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.cqrs.Query;

public record GetTodoQuery(TodoId id) implements Query {
}
