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

@Path("registration")
public class RegistrationResource {

    private static final Logger LOG = Logger.getLogger(RegistrationResource.class);

    @Inject
    UserService userService;

    @Location("registration/registration.qute.html")
    Template signUpForm;

    @Location("authentication/authentication.qute.html")
    Template signInForm;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return signUpForm.instance();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Uni<TemplateInstance> create(@Valid @BeanParam RegistrationRequest registrationRequest) {
        LOG.infov("processing registration request for user[{0}]", registrationRequest.getUsername());
        return userService.createUser(registrationRequest)
                .invoke(u -> LOG.infov("successfully registered new user[{0}]", u.getUsername()))
                .map(u -> signInForm.data("username", u.getUsername()));
    }

}
