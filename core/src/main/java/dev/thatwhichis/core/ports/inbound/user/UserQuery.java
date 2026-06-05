package dev.thatwhichis.core.ports.inbound.user;

import dev.thatwhichis.framework.cqrs.Query;

import java.util.UUID;

sealed public interface UserQuery extends Query {

    record Show(UUID userId) implements UserQuery {

    }
}
