package space.nanobreaker.user.core.domain;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.jpa.user.UserEntity;
import space.nanobreaker.jpa.user.UserRepository;
import space.nanobreaker.user.adapter.rest.RegistrationRequest;

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
    public Uni<UserEntity> createUser(final RegistrationRequest registrationRequest) {
        final UserEntity user = new UserEntity();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());

        return userRepository.persist(user);
    }

}
