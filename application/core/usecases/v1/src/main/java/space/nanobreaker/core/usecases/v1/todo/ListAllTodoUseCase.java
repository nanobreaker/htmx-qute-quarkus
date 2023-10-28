package space.nanobreaker.core.usecases.v1.todo;

import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.v1.Request;
import space.nanobreaker.core.usecases.v1.Response;
import space.nanobreaker.core.usecases.v1.UseCase;

import java.util.List;

public interface ListAllTodoUseCase extends UseCase<ListAllTodoUseCase.ListAllTodoUseCaseRequest, ListAllTodoUseCase.ListAllTodoUseCaseResponse> {

    record ListAllTodoUseCaseRequest() implements Request {
    }

    record ListAllTodoUseCaseResponse(List<Todo> todos) implements Response {
    }

}
