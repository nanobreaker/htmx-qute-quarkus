package space.nanobreaker.core.usecases.v1.todo;

import space.nanobreaker.core.usecases.v1.Request;
import space.nanobreaker.core.usecases.v1.Response;
import space.nanobreaker.core.usecases.v1.UseCase;

import java.time.LocalDate;
import java.util.UUID;

public interface CreateTodoUseCase extends UseCase<CreateTodoUseCase.CreateTodoRequest, CreateTodoUseCase.CreateTodoResponse> {

    record CreateTodoRequest(String title, String description, LocalDate target) implements Request {
    }

    record CreateTodoResponse(UUID id) implements Response {
    }

}
