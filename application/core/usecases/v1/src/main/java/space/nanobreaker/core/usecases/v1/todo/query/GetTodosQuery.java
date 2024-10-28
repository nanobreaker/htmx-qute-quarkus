package space.nanobreaker.core.usecases.v1.todo.query;

import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.cqrs.Query;
import space.nanobreaker.library.either.Either;

import java.util.Set;

public record GetTodosQuery(
        Either<Set<TodoId>, String> idsOrUsername
) implements Query {

}