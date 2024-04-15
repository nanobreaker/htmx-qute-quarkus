
package space.nanobreaker.configuration.microservice.user.resources.dashboard;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("dashboard")
public class DashboardResource {

  @Inject
  EventBus eventBus;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Uni<String> get() {
    return DashboardTemplates.dashboard().createUni();
  }
}
