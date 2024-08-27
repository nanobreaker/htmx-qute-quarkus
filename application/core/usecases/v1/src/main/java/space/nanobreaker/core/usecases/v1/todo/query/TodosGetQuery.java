package space.nanobreaker.core.usecases.v1.todo.query;

import space.nanobreaker.cqrs.Query;

import java.util.Set;

public record TodosGetQuery(
        String username,
        Set<Integer> ids
) implements Query {
}