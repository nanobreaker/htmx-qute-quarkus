package space.nanobreaker.core.usecases.v1.user.query;

import space.nanobreaker.core.usecases.v1.Query;

import java.util.UUID;

public record GetUserQuery(UUID id) implements Query {
}
