package space.nanobreaker.auth.adapter.rest;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import space.nanobreaker.user.core.domain.UserService;

@Path("auth")
@Produces(MediaType.TEXT_HTML)
public class AuthenticationResource {

    @Inject
    UserService userService;

    @Location("auth/authentication.qute.html")
    Template authenticationTemplate;

    @Location("auth/registration.qute.html")
    Template registrationTemplate;

    @GET
    @PermitAll
    @Path("authenticate")
    public Uni<TemplateInstance> getAuthenticationTemplate() {
        return Uni.createFrom().item(authenticationTemplate.instance());
    }

    @GET
    @PermitAll
    @Path("register")
    public Uni<TemplateInstance> getRegistrationTemplate() {
        return Uni.createFrom().item(registrationTemplate.instance());
    }

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("register")
    public Uni<TemplateInstance> register(@Valid @BeanParam RegistrationRequest registrationRequest) {
        return userService.createUser(registrationRequest.mapToUserEntity())
                .map(u -> authenticationTemplate.data("username", u.getUsername()))
                .log("registration request");
    }

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("authenticate")
    public Uni<Response> authenticate(@Valid @BeanParam AuthenticationRequest authenticationRequest) {
        return userService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword())
                .map(user -> Response.ok()
                        .header("HX-Refresh", true)
                        .cookie(new NewCookie.Builder("access-token")
                                .path("/")
                                .domain("localhost")
                                .maxAge(86400)
                                .value(userService.buildJwtForUser(user))
                                .comment("jwt access token used for authorization")
                                .httpOnly(true)
                                .build())
                        .build())
                .log("authentication request");
    }

}
