package space.nanobreaker.configuration.microservice.user.resources.entry;

import io.quarkus.oidc.IdToken;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class EntryResource {

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Location("v1/entry/authorized.qute.html")
    Template authorizedTemplate;

    @GET
    @RolesAllowed({"user"})
    public Uni<TemplateInstance> getAuthorized() {
        return Uni.createFrom().item(authorizedTemplate.data("username", idToken.getClaim("preferred_username")));

    }

    @GET
    @RolesAllowed({"user"})
    @Path("entry")
    public Uni<TemplateInstance> get() {
        return Uni.createFrom().item(authorizedTemplate.data("username", idToken.getClaim("preferred_username")));
    }

}
