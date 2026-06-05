package dev.thatwhichis.framework.cqrs;

import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

public interface CommandHandler<C extends Command, R> {

    Uni<Result<R, Error>> handle(C command);
}