package space.nanobreaker.core.usecases.v1.user.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.User;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;
import space.nanobreaker.core.usecases.v1.CommandHandler;
import space.nanobreaker.core.usecases.v1.user.command.AuthenticateUserCommand;

import java.util.UUID;

@ApplicationScoped
public class AuthenticateUserCommandHandler implements CommandHandler<AuthenticateUserCommand, UUID> {

    @Inject
    @Any
    UserRepository userRepository;

    @ConsumeEvent(value = "authenticateUserCommand")
    @WithTransaction
    public Uni<UUID> execute(final AuthenticateUserCommand command) {
        return userRepository.findByUsername(command.username())
                .onItem()
                .ifNull().failWith(() -> new IllegalArgumentException("authentication failed: no user with such username"))
                .invoke(user -> authenticate(user.getPassword(), command.password()))
                .map(User::getId);
    }

    private void authenticate(final String expected, final String actual) {
        if (!expected.equals(actual)) {
            throw new IllegalArgumentException("authentication failed: password doesn't match");
        }
    }

}
