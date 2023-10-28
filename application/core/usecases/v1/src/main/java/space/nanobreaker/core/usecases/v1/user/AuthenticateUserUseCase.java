package space.nanobreaker.core.usecases.v1.user;

import space.nanobreaker.core.usecases.v1.Request;
import space.nanobreaker.core.usecases.v1.Response;
import space.nanobreaker.core.usecases.v1.UseCase;

import java.util.UUID;

public interface AuthenticateUserUseCase extends UseCase<AuthenticateUserUseCase.AuthenticateUserUseCaseRequest, AuthenticateUserUseCase.AuthenticateUserUseCaseResponse> {

    record AuthenticateUserUseCaseRequest(String username, String password) implements Request {

    }

    record AuthenticateUserUseCaseResponse(UUID id) implements Response {

    }

}
