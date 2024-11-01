package space.nanobreaker.cqrs;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.result.Result;

public interface CommandHandler<C extends Command, R extends Result> {

    Uni<R> handle(C command);

}