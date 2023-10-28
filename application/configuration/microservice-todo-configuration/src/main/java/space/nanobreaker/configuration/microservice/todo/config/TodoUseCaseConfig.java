package space.nanobreaker.configuration.microservice.todo.config;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.core.usecases.v1.todo.CreateTodoUseCase;
import space.nanobreaker.core.usecases.v1.todo.ListAllTodoUseCase;
import space.nanobreaker.core.usecases.v1.todo.impl.CreateTodoUseCaseImpl;
import space.nanobreaker.core.usecases.v1.todo.impl.ListAllTodoUseCaseImpl;

@Dependent
public class TodoUseCaseConfig {

    @Inject
    @Any
    TodoRepository todoRepository;

    @Produces
    @DefaultBean
    public CreateTodoUseCase createDefaultCreateTodoUseCase() {
        return new CreateTodoUseCaseImpl(todoRepository);
    }

    @Produces
    @DefaultBean
    public ListAllTodoUseCase createDefaultListAllTodoUseCase() {
        return new ListAllTodoUseCaseImpl(todoRepository);
    }

}
