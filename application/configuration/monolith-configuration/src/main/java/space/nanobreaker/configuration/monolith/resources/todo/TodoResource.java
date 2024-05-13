package space.nanobreaker.configuration.monolith.resources.todo;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import space.nanobreaker.configuration.monolith.dto.todo.CreateTodoRequestTO;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.domain.v1.TodoId;
import space.nanobreaker.core.usecases.v1.todo.command.CompleteTodoCommand;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodoCommand;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodoQuery;

import java.util.List;

@Path("todo")
public class TodoResource {

    @Inject
    EventBus eventBus;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("board")
    public Uni<String> getBoard() {
        return renderTodoBoard();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> create(@Valid @BeanParam CreateTodoRequestTO createTodoRequest) {
        return eventBus.<TodoId>request("todo.create", createTodoRequest.mapToCommand())
                .onItem()
                .transformToUni(response -> eventBus.<Todo>request("getTodoQuery", new GetTodoQuery(response.body())))
                .onItem()
                .transformToUni(this::renderTodo)
                .log("create todo request");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Path("{id}/complete")
    public Uni<String> complete(@PathParam("id") Long id) {
        final TodoId todoId = new TodoId(id, securityIdentity.getPrincipal().getName());
        final CompleteTodoCommand completeTodoCommand = new CompleteTodoCommand(todoId);
        return eventBus.<TodoId>request("completeTodoCommand", completeTodoCommand)
                .onItem()
                .transformToUni(response -> eventBus.<Todo>request("getTodoQuery", new GetTodoQuery(response.body())))
                .onItem()
                .transformToUni(this::renderTodo)
                .log("complete todo request");
    }

    @DELETE
    @Produces(MediaType.TEXT_HTML)
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        final TodoId todoId = new TodoId(id, securityIdentity.getPrincipal().getName());
        final DeleteTodoCommand deleteTodoCommand = new DeleteTodoCommand(todoId);
        return eventBus.<TodoId>request("deleteTodoCommand", deleteTodoCommand)
                .map(deletedId -> Response.ok().build())
                .log("delete todo request");
    }

    @WithSpan("renderTodoBoardTemplate")
    public Uni<String> renderTodoBoard() {
        return TodoTemplates.todoBoard(List.of()).createUni();
    }

    @WithSpan("renderTodoTemplate")
    public Uni<String> renderTodo(Message<Todo> response) {
        return TodoTemplates.todoBoard$todo(response.body()).createUni();
    }
}
