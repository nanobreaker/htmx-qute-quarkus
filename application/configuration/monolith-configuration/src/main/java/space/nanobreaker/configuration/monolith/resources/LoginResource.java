package space.nanobreaker.configuration.monolith.resources;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.Cache;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {

    @Location("login/login.qute.html")
    Template template;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> login() {
        return template.instance()
                .createUni();
    }
}