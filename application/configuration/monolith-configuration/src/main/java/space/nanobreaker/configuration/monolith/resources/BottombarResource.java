
package space.nanobreaker.configuration.monolith.resources;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.Cache;
import space.nanobreaker.configuration.monolith.templates.BottombarTemplates;

@Path("bottombar")
public class BottombarResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> get() {
        return BottombarTemplates.bottombar()
                .createUni();
    }
}
