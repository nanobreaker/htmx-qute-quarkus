package space.nanobreaker.todo.core.domain;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import space.nanobreaker.jpa.todo.TodoEntity;
import space.nanobreaker.jpa.todo.TodoRepository;
import space.nanobreaker.todo.adapter.rest.CreateTodoRequest;

import java.util.List;

@ApplicationScoped
public class TodoService {

    @Inject
    TodoRepository todoRepository;

    @WithTransaction
    public Uni<TodoEntity> create(final CreateTodoRequest createTodoRequest) {
        final TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTitle(createTodoRequest.getTitle());
        todoEntity.setDescription(createTodoRequest.getDescription());
        todoEntity.setTarget(createTodoRequest.getTarget());

        return todoRepository.persist(todoEntity);
    }

    @WithSession
    public Uni<List<TodoEntity>> listAll() {
        return todoRepository.listAll();
    }

}
