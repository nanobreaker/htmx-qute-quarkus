package dev.thatwhichis.core.ports.inbound;

import dev.thatwhichis.framework.cqrs.Query;

sealed public interface UserQuery extends Query {

    record Show() implements UserQuery {

    }
}
