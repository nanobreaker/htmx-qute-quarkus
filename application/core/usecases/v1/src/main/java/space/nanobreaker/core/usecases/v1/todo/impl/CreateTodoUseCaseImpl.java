package space.nanobreaker.core.usecases.v1.todo.impl;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.CreateTodoUseCase;

public class CreateTodoUseCaseImpl implements CreateTodoUseCase {

    TodoRepository todoRepository;

    public CreateTodoUseCaseImpl(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Uni<CreateTodoResponse> execute(CreateTodoRequest request) {
        final Todo todo = mapCreateTodoRequestToTodoModel(request);
        return todoRepository
                .persist(todo)
                .map(this::mapTodoModelToCreateTodoResponse);
    }

    private Todo mapCreateTodoRequestToTodoModel(final CreateTodoRequest request) {
        return Todo.TodoBuilder.aTodo()
                .withTitle(request.title())
                .withDescription(request.description())
                .withTarget(request.target())
                .build();
    }

    private CreateTodoResponse mapTodoModelToCreateTodoResponse(final Todo todo) {
        return new CreateTodoResponse(todo.getId());
    }

}
