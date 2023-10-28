package space.nanobreaker.core.usecases.v1.user;

import space.nanobreaker.core.usecases.v1.Request;
import space.nanobreaker.core.usecases.v1.Response;
import space.nanobreaker.core.usecases.v1.UseCase;

import java.util.UUID;

public interface RegisterUserUseCase extends UseCase<RegisterUserUseCase.RegisterUserUseCaseRequest, RegisterUserUseCase.RegisterUserUseCaseResponse> {

    record RegisterUserUseCaseRequest(String username, String password) implements Request {
    }

    record RegisterUserUseCaseResponse(UUID id) implements Response {

    }

}
