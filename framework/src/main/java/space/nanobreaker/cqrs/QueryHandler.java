package space.nanobreaker.cqrs;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.error.Error;
import io.github.dcadea.jresult.Result;

public interface QueryHandler<Q extends Query, R> {

    Uni<Result<R, Error>> execute(Q query);

}