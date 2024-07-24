package space.nanobreaker.cqrs;

import space.nanobreaker.library.Error;
import space.nanobreaker.library.Result;

public interface CommandHandler<C extends Command> {

    Result<Void, Error> execute(C command);

}