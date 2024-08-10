package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.core.usecases.v1.todo.query.TodoGetQuery;
import space.nanobreaker.library.Err;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Ok;
import space.nanobreaker.library.Result;

@Path("todo")
public class TodoResource {

    @Inject
    EventBus eventBus;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> get(@PathParam("id") Integer id) {
        final TodoId todoId = new TodoId(id, securityIdentity.getPrincipal().getName());
        final TodoGetQuery query = new TodoGetQuery(todoId);
        return eventBus.<Result<Todo, Error>>request("todo.get", query)
                .map(Message::body)
                .map(result -> switch (result) {
                    case Ok(final Todo todo) -> TodoTemplates.todo(todo, false).render();
                    case Err(final Error error) -> "empty";
                });
    }
}
