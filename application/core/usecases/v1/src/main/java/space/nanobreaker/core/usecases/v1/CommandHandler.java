package space.nanobreaker.core.usecases.v1;

import io.smallrye.mutiny.Uni;

public interface CommandHandler<C extends Command, R> {

    Uni<R> execute(C command);

}
