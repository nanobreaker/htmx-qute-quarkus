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
import space.nanobreaker.core.usecases.v1.user.command.RegisterUserCommand;

import java.util.UUID;

@ApplicationScoped
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, UUID> {

    @Inject
    @Any
    UserRepository userRepository;

    @ConsumeEvent(value = "registerUserCommand")
    @WithTransaction
    public Uni<UUID> execute(final RegisterUserCommand command) {
        final User user = mapRegisterUserCommandToUser(command);
        return userRepository
                .persist(user)
                .map(User::getId);
    }

    private User mapRegisterUserCommandToUser(final RegisterUserCommand request) {
        return User.UserBuilder.anUser()
                .withUsername(request.username())
                .withPassword(request.password())
                .build();
    }

}
