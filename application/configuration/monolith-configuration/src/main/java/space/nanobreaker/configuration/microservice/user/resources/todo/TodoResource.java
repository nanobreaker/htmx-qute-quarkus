package space.nanobreaker.configuration.microservice.user.resources.todo;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import space.nanobreaker.configuration.microservice.user.dto.todo.CreateTodoRequestTO;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.v1.todo.command.CompleteTodoCommand;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodoCommand;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodoQuery;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodosQuery;

import java.util.List;
import java.util.UUID;

@Path("todo")
@RolesAllowed({"user"})
public class TodoResource {

    @Inject
    EventBus eventBus;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("todo-form")
    public Uni<String> getTodoForm() {
        return TodoTemplates.todoForm()
                .createUni()
                .log("get todo form");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("all")
    public Uni<String> listAll() {
        return eventBus.<List<Todo>>request("getTodosQuery", new GetTodosQuery())
                .onItem()
                .transformToUni(response -> TodoTemplates.todos(response.body()).createUni())
                .log("get all todos");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> create(@Valid @BeanParam CreateTodoRequestTO createTodoRequest) {
        return eventBus.<UUID>request("createTodoCommand", createTodoRequest.mapToCreateTodoCommand())
                .onItem()
                .transformToUni(response -> eventBus.<Todo>request("getTodoQuery", new GetTodoQuery(response.body())))
                .onItem()
                .transformToUni(response -> TodoTemplates.todos$todo(response.body()).createUni())
                .log("create todo request");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Path("{id}/complete")
    public Uni<String> complete(@PathParam("id") UUID id) {
        return eventBus.<UUID>request("completeTodoCommand", new CompleteTodoCommand(id))
                .onItem()
                .transformToUni(response -> eventBus.<Todo>request("getTodoQuery", new GetTodoQuery(response.body())))
                .onItem()
                .transformToUni(response -> TodoTemplates.todos$todo(response.body()).createUni())
                .log("complete todo request");
    }

    @DELETE
    @Produces(MediaType.TEXT_HTML)
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return eventBus.<UUID>request("deleteTodoCommand", new DeleteTodoCommand(id))
                .map(deletedId -> Response.ok().build())
                .log("delete todo request");
    }

}
