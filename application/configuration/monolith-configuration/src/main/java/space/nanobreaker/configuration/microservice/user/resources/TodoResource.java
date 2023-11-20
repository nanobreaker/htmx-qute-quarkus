package space.nanobreaker.configuration.microservice.user.resources;

import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import space.nanobreaker.configuration.microservice.user.dto.CreateTodoRequestTO;
import space.nanobreaker.core.domain.v1.Todo;
import space.nanobreaker.core.usecases.v1.todo.command.CompleteTodoCommand;
import space.nanobreaker.core.usecases.v1.todo.command.DeleteTodoCommand;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodoQuery;
import space.nanobreaker.core.usecases.v1.todo.query.GetTodosQuery;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Path("todo")
public class TodoResource {

    @Inject
    EventBus eventBus;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("todo-form")
    @RolesAllowed({"User"})
    public Uni<String> getTodoForm() {
        return TodoTemplates.todoForm()
                .createUni()
                .log("get todo form");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    @Path("all")
    public Uni<String> listAll() {
        return Uni.createFrom()
                .completionStage(
                        eventBus.<List<Todo>>request("getTodosQuery", new GetTodosQuery())
                                .toCompletionStage())
                .map(message -> message.body()
                        .stream()
                        .sorted(Comparator.comparing(Todo::getTarget))
                        .collect(Collectors.toList()))
                .onItem()
                .transformToUni(todos -> TodoTemplates.todos(todos).createUni())
                .log("get all todos");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    public Uni<String> create(@Valid @BeanParam CreateTodoRequestTO createTodoRequest) {
        return Uni.createFrom()
                .completionStage(submitCommand("createTodoCommand", createTodoRequest.mapToCreateTodoCommand()))
                .onItem()
                .transformToUni(response -> Uni.createFrom()
                        .completionStage(eventBus.<Todo>request("getTodoQuery", new GetTodoQuery(response.body()))
                                .toCompletionStage()
                        )
                )
                .onItem()
                .transformToUni(response -> TodoTemplates.todos$todo(response.body()).createUni())
                .log("create todo request");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    @Path("{id}/complete")
    public Uni<String> complete(@PathParam("id") UUID id) {
        return Uni.createFrom()
                .completionStage(submitCommand("completeTodoCommand", new CompleteTodoCommand(id)))
                .onItem()
                .transformToUni(response -> Uni.createFrom()
                        .completionStage(eventBus.<Todo>request("getTodoQuery", new GetTodoQuery(response.body()))
                                .toCompletionStage()
                        )
                )
                .onItem()
                .transformToUni(response -> TodoTemplates.todos$todo(response.body()).createUni())
                .log("complete todo request");
    }

    @DELETE
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"User"})
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return Uni.createFrom()
                .completionStage(submitCommand("deleteTodoCommand", new DeleteTodoCommand(id)))
                .map(deleted -> Response.ok().build())
                .log("delete todo request");
    }

    private CompletionStage<Message<UUID>> submitCommand(final String address, final Object command) {
        return eventBus.<UUID>request(address, command)
                .toCompletionStage();
    }

}
