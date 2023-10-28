package space.nanobreaker.core.usecases.v1.user.impl;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.User;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;
import space.nanobreaker.core.usecases.v1.user.RegisterUserUseCase;

public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    UserRepository userRepository;

    public RegisterUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Uni<RegisterUserUseCaseResponse> execute(RegisterUserUseCaseRequest request) {
        final User user = mapRegisterUserUseCaseRequestToUser(request);
        return userRepository
                .persist(user)
                .map(this::mapUserModelToRegisterUserUseCaseResponse);
    }

    private User mapRegisterUserUseCaseRequestToUser(final RegisterUserUseCaseRequest request) {
        return User.UserBuilder.anUser()
                .withUsername(request.username())
                .withPassword(request.password())
                .build();
    }

    private RegisterUserUseCaseResponse mapUserModelToRegisterUserUseCaseResponse(final User user) {
        return new RegisterUserUseCaseResponse(user.getId());
    }

}
