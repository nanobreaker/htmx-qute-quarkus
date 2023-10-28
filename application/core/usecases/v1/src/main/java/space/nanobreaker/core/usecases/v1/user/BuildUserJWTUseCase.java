package space.nanobreaker.core.usecases.v1.user;

import space.nanobreaker.core.usecases.v1.Request;
import space.nanobreaker.core.usecases.v1.Response;
import space.nanobreaker.core.usecases.v1.UseCase;

import java.util.UUID;

import static space.nanobreaker.core.usecases.v1.user.BuildUserJWTUseCase.BuildUserJWTUseCaseRequest;
import static space.nanobreaker.core.usecases.v1.user.BuildUserJWTUseCase.BuildUserJWTUseCaseResponse;

public interface BuildUserJWTUseCase extends UseCase<BuildUserJWTUseCaseRequest, BuildUserJWTUseCaseResponse> {

    record BuildUserJWTUseCaseRequest(UUID userId) implements Request {

    }

    record BuildUserJWTUseCaseResponse(String token) implements Response {
    }

}
