package space.nanobreaker.core.usecases.v1.todo.impl;

import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.ListAllTodoUseCase;

import java.util.List;

public class ListAllTodoUseCaseImpl implements ListAllTodoUseCase {

    TodoRepository todoRepository;

    public ListAllTodoUseCaseImpl(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Uni<ListAllTodoUseCaseResponse> execute(ListAllTodoUseCaseRequest request) {
        return todoRepository.listAll()
                .map(this::mapTodosToListAllTodoUseCaseResponse);
    }

    private ListAllTodoUseCaseResponse mapTodosToListAllTodoUseCaseResponse(List<Todo> todos) {
        return new ListAllTodoUseCaseResponse(todos);
    }
}
