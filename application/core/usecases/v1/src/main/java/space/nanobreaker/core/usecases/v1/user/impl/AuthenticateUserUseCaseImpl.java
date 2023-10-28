package space.nanobreaker.core.usecases.v1.user.impl;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;
import space.nanobreaker.core.usecases.v1.user.AuthenticateUserUseCase;

public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    UserRepository userRepository;

    public AuthenticateUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Uni<AuthenticateUserUseCaseResponse> execute(AuthenticateUserUseCaseRequest request) {
        return userRepository.findByUsername(request.username())
                .onItem()
                .ifNull().failWith(() -> new IllegalArgumentException("authentication failed: no user with such username"))
                .invoke(user -> authenticate(user.getPassword(), request.password()))
                .map(userModel -> new AuthenticateUserUseCaseResponse(userModel.getId()));
    }

    private void authenticate(final String expected, final String actual) {
        if (!expected.equals(actual)) {
            throw new IllegalArgumentException("authentication failed: password doesn't match");
        }
    }
}
