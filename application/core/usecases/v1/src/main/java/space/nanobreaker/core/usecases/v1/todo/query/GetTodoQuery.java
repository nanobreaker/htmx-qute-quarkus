package space.nanobreaker.core.usecases.v1.todo.query;

import space.nanobreaker.core.usecases.v1.Query;

import java.util.UUID;

public record GetTodoQuery(UUID id) implements Query {
}
