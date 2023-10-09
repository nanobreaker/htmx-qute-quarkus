package space.nanobreaker.todo.adapter.rest;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import space.nanobreaker.todo.core.domain.TodoService;

@Path("todo")
public class TodoResource {

    private static final Logger LOG = Logger.getLogger(TodoResource.class);

    @Location("v1/todo/todo-form.qute.html")
    Template todoForm;

    @Location("v1/todo/todo-grid.qute.html")
    Template todoGrid;

    @Location("v1/todo/todo.qute.html")
    Template todo;

    @Inject
    TodoService todoService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("todo-form")
    @RolesAllowed({"User"})
    public Uni<TemplateInstance> get() {
        LOG.info("requesting board");
        return Uni.createFrom().item(() -> todoForm.instance());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    public Uni<TemplateInstance> getAll() {
        return todoService.listAll()
                .map(todoEntities -> todoGrid.data("todos", todoEntities));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    public Uni<TemplateInstance> create(@Valid @BeanParam CreateTodoRequest createTodoRequest) {
        LOG.infov("request to create a new todo with details [title:{0}, description:{1}], untilDate:{2}]",
                createTodoRequest.getTitle(),
                createTodoRequest.getDescription(),
                createTodoRequest.getTarget().toString()
        );
        return todoService.create(createTodoRequest)
                .map(todoEntity -> todo.data("todo", todoEntity));
    }

}
