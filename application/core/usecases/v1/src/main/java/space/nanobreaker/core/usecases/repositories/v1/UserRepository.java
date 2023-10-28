package space.nanobreaker.core.usecases.repositories.v1;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.User;

import java.util.UUID;

public interface UserRepository {

    Uni<User> persist(User userEntity);

    Uni<User> findByUserId(UUID id);

    Uni<User> findByUsername(String username);

}
