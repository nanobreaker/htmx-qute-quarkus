package space.nanobreaker.cqrs;

import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.error.Error;

public interface QueryHandler<Q extends CQRSEvent, R> {

    Uni<Result<R, Error>> execute(Q query);
}