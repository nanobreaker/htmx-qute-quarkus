package space.nanobreaker.configuration.microservice.user.resources;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Objects;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class EntryController {

    @Inject
    JWTParser jwtParser;

    @Location("v1/entry/anonymous.qute.html")
    Template anonymousTemplate;

    @Location("v1/entry/authorized.qute.html")
    Template authorizedTemplate;

    @GET
    @PermitAll
    public Uni<TemplateInstance> get(@CookieParam("access-token") String jwtCookie) throws ParseException {
        if (authorized(jwtCookie)) {
            JsonWebToken parse = jwtParser.parse(jwtCookie);
            return Uni.createFrom().item(authorizedTemplate.data("username", parse.getName()));
        }

        return Uni.createFrom().item(anonymousTemplate.instance());
    }

    private boolean authorized(final String jwtCookie) {
        if (Objects.isNull(jwtCookie)) return false;

        try {
            jwtParser.parse(jwtCookie);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

}
