package space.nanobreaker.cqrs;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

public interface QueryHandler<Q extends Query, R> {

    Uni<Result<R, Error>> execute(Q query);

}