package space.nanobreaker.configuration.microservice.user.config;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;
import space.nanobreaker.core.usecases.v1.user.AuthenticateUserUseCase;
import space.nanobreaker.core.usecases.v1.user.BuildUserJWTUseCase;
import space.nanobreaker.core.usecases.v1.user.RegisterUserUseCase;
import space.nanobreaker.core.usecases.v1.user.impl.AuthenticateUserUseCaseImpl;
import space.nanobreaker.core.usecases.v1.user.impl.BuildUserJWTUseCaseImpl;
import space.nanobreaker.core.usecases.v1.user.impl.RegisterUserUseCaseImpl;

@Dependent
public class UserUseCaseConfig {

    @Inject
    @Any
    UserRepository userRepository;

    @Produces
    @DefaultBean
    public RegisterUserUseCase createDefaultRegisterUserUseCase() {
        return new RegisterUserUseCaseImpl(userRepository);
    }

    @Produces
    @DefaultBean
    public AuthenticateUserUseCase createDefaultAuthenticateUserUseCase() {
        return new AuthenticateUserUseCaseImpl(userRepository);
    }

    @Produces
    @DefaultBean
    public BuildUserJWTUseCase createDefaultBuildUserJWTUseCase() {
        return new BuildUserJWTUseCaseImpl(userRepository);
    }

}
