
package space.nanobreaker.configuration.monolith.resources.dashboard;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("dashboard")
public class DashboardResource {

    @Inject
    SecurityIdentity identity;

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @ConfigProperty(name = "quarkus.application.version")
    String applicationVersion;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> get() {
        return DashboardTemplates.dashboard(
                        applicationName,
                        applicationVersion,
                        identity.getPrincipal().getName())
                .createUni();
    }

    @Path("v2")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getV2() {
        return Dashboard2Templates.dashboard2(
                        applicationName,
                        applicationVersion,
                        identity.getPrincipal().getName()
                )
                .render();
    }
}
