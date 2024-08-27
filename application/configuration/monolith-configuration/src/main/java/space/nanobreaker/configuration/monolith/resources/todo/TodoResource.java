package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
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
import space.nanobreaker.core.usecases.v1.todo.query.TodosGetQuery;
import space.nanobreaker.library.Err;
import space.nanobreaker.library.Error;
import space.nanobreaker.library.Ok;
import space.nanobreaker.library.Result;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Path("todo")
public class TodoResource {

    @Inject
    EventBus eventBus;

    @Inject
    SecurityIdentity securityIdentity;

    @Location("exception/error.qute.html")
    Template error;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> all() {
        final String username = securityIdentity.getPrincipal().getName();
        final TodosGetQuery query = new TodosGetQuery(username, Set.of());

        return eventBus.<Result<Set<Todo>, Error>>request("todos.get", query)
                .map(Message::body)
                .map(result -> switch (result) {
                    case Ok(final Set<Todo> todos) -> {
                        final Set<Todo> sorted = todos.stream()
                                .sorted(Comparator.comparingInt(o -> o.getId().getId()))
                                .collect(Collectors.toCollection(LinkedHashSet::new));
                        yield TodoTemplates.todoGrid(sorted, false).render();
                    }
                    case Err(final Error err) -> error.data("error", err.getClass().getName()).render();
                });
    }

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
                    case Err(final Error err) -> error.data("error", err.getClass().getName()).render();
                });
    }
}
