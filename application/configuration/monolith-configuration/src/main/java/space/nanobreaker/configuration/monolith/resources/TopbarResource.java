
package space.nanobreaker.configuration.monolith.resources;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.Cache;
import space.nanobreaker.configuration.monolith.templates.TopbarTemplates;

@Path("topbar")
public class TopbarResource {

    @Inject JsonWebToken jwt;
    @ConfigProperty(name = "quarkus.application.name") String name;
    @ConfigProperty(name = "quarkus.application.version") String version;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> get() {
        final String upn = jwt.getClaim("upn");

        return TopbarTemplates.topbar(name, version, upn)
                .createUni();
    }
}
