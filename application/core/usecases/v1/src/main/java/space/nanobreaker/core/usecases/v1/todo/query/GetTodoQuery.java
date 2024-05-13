package space.nanobreaker.core.usecases.v1.todo.query;

import space.nanobreaker.core.domain.v1.TodoId;
import space.nanobreaker.core.usecases.v1.Query;

import java.util.UUID;

public record GetTodoQuery(TodoId id) implements Query {
}
