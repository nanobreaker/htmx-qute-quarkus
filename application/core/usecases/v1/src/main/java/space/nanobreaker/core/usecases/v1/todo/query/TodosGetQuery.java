package space.nanobreaker.core.usecases.v1.todo.query;

import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.cqrs.Query;
import space.nanobreaker.library.Either;

import java.util.Set;

public record TodosGetQuery(
        Either<String, Set<TodoId>> usernameOrIds
) implements Query {
}