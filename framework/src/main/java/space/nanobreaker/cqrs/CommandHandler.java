package space.nanobreaker.cqrs;

import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

public interface CommandHandler<C extends CQRSEvent, R extends Result> {

    Uni<R> handle(C command);
}