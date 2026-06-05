package dev.thatwhichis.framework.cqrs;

import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

public interface QueryHandler<Q extends Query, R> {

    Uni<Result<R, Error>> execute(Q query);
}