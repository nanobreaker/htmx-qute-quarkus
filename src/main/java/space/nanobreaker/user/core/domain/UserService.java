package space.nanobreaker.user.core.domain;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.user.jpa.UserEntity;
import space.nanobreaker.user.jpa.UserRepository;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @WithSession
    public Uni<UserEntity> authenticate(final String username, final String password) {
        return userRepository.findByUsername(username)
                .onItem()
                .ifNull().failWith(() -> new IllegalArgumentException("user not found"))
                .invoke(user -> authenticate(user, password));
    }

    private void authenticate(final UserEntity user, final String providedPasswords) {
        if (!user.getPassword().equals(providedPasswords)) {
            throw new IllegalArgumentException("authentication failed: password doesn't match");
        }
    }

    @WithTransaction
    public Uni<UserEntity> createUser(final UserEntity user) {
        return userRepository.persist(user);
    }

}
