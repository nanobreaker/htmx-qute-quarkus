package space.nanobreaker.core.usecases.v1.user.impl;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.User;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;
import space.nanobreaker.core.usecases.v1.user.BuildUserJWTUseCase;

public class BuildUserJWTUseCaseImpl implements BuildUserJWTUseCase {

    UserRepository userRepository;

    public BuildUserJWTUseCaseImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Uni<BuildUserJWTUseCaseResponse> execute(BuildUserJWTUseCaseRequest request) {
        return userRepository.findByUserId(request.userId())
                .map(this::buildJWT)
                .map(BuildUserJWTUseCaseResponse::new);
    }

    private String buildJWT(final User user) {
        return Jwt.issuer("https://example.com/issuer")
                .upn(user.getUsername())
                .groups("User")
                .sign();
    }

}
