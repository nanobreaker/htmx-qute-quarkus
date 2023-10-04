package space.nanobreaker.user.adapter.rest;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import space.nanobreaker.user.core.domain.UserService;

@Path("authentication")
public class AuthenticationResource {

    private static final Logger LOG = Logger.getLogger(AuthenticationResource.class);

    @Inject
    UserService userService;

    @Location("authentication/authentication.qute.html")
    Template signInForm;

    @Location("todo/todo-board.qute.html")
    Template board;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("authentication-form")
    public TemplateInstance get() {
        LOG.info("processing get authentication form request");
        return signInForm.instance();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<TemplateInstance> authenticate(@Valid @BeanParam AuthenticationRequest authenticateUserTO) {
        LOG.infov("processing authentication request for user[{0}]", authenticateUserTO.getUsername());
        return userService.authenticate(authenticateUserTO.getUsername(), authenticateUserTO.getPassword())
                .invoke(u -> LOG.infov("successfully authenticated user[{0}]", u.getUsername()))
                .map(authenticated -> board.instance());
    }

}
