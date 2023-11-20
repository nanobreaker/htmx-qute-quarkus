package space.nanobreaker.core.usecases.v1;

import io.smallrye.mutiny.Uni;

public interface QueryHandler<Q extends Query, R> {

    Uni<R> execute(Q query);

}
