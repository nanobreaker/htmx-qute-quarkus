package space.nanobreaker.infra.dataproviders.postgres.repositories;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.User;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    Map<UUID, User> userEntityConcurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public Uni<User> persist(User userEntity) {
        return Uni.createFrom()
                .item(() -> userEntityConcurrentHashMap.put(UUID.randomUUID(), userEntity));
    }

    @Override
    public Uni<User> findByUserId(UUID id) {
        return Uni.createFrom()
                .item(() -> userEntityConcurrentHashMap.get(id));
    }

    public Uni<User> findByUsername(String username) {
        return Uni.createFrom()
                .item(() -> userEntityConcurrentHashMap.entrySet()
                        .stream()
                        .filter(user -> user.getValue().getUsername().equals(username))
                        .findFirst()
                        .map(Map.Entry::getValue)
                        .orElse(null)
                );
    }

}
