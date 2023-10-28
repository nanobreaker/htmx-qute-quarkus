package space.nanobreaker.configuration.microservice.user.resources;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import space.nanobreaker.configuration.microservice.user.dto.CreateTodoRequestTO;
import space.nanobreaker.core.usecases.v1.todo.CreateTodoUseCase;
import space.nanobreaker.core.usecases.v1.todo.ListAllTodoUseCase;

@Path("todo")
public class TodoResource {

    @Location("v1/todo/todo-form.qute.html")
    Template todoForm;
    @Location("v1/todo/todo-grid.qute.html")
    Template todoGrid;
    @Location("v1/todo/todo.qute.html")
    Template todo;

    @Inject
    @Any
    CreateTodoUseCase createTodoUseCase;
    @Inject
    @Any
    ListAllTodoUseCase listAllTodoUseCase;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("todo-form")
    @RolesAllowed({"User"})
    public Uni<TemplateInstance> getTodoForm() {
        return Uni.createFrom()
                .item(() -> todoForm.instance());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    @WithSession
    public Uni<TemplateInstance> listAll() {
        return listAllTodoUseCase.execute(new ListAllTodoUseCase.ListAllTodoUseCaseRequest())
                .map(todos -> todoGrid.data("todos", todos));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    @WithTransaction
    public Uni<TemplateInstance> create(@Valid @BeanParam CreateTodoRequestTO createTodoRequest) {
        return createTodoUseCase.execute(createTodoRequest.createRequest())
                .map(todo -> this.todo.data("todo", todo));
    }

}
