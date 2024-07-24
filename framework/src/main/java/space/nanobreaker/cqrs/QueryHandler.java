package space.nanobreaker.cqrs;

import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

public interface QueryHandler<Q extends Query, R> {

    Result<R, Error> execute(Q query);

}