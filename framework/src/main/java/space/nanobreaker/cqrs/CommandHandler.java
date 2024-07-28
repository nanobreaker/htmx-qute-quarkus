package space.nanobreaker.cqrs;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

public interface CommandHandler<C extends Command> {

    Uni<Result<Void, Error>> handle(C command);

}