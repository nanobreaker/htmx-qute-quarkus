package dev.thatwhichis.core.ports.outbound.user;

import dev.thatwhichis.core.domain.user.User;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface UserRepository {

    Uni<Result<Option<User>, Error>> find(UUID id);

    Uni<Result<User, Error>> save(User user);
}
