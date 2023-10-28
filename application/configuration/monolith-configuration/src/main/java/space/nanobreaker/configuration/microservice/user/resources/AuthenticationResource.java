package space.nanobreaker.configuration.microservice.user.resources;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import space.nanobreaker.configuration.microservice.user.dto.AuthenticationRequestTO;
import space.nanobreaker.configuration.microservice.user.dto.RegistrationRequestTO;
import space.nanobreaker.core.usecases.v1.user.AuthenticateUserUseCase;
import space.nanobreaker.core.usecases.v1.user.BuildUserJWTUseCase;
import space.nanobreaker.core.usecases.v1.user.RegisterUserUseCase;

@Path("auth")
@Produces(MediaType.TEXT_HTML)
public class AuthenticationResource {

    @Location("v1/auth/authentication.qute.html")
    Template authenticationTemplate;

    @Location("v1/auth/registration.qute.html")
    Template registrationTemplate;

    @Inject
    @Any
    RegisterUserUseCase registerUserUseCase;

    @Inject
    @Any
    AuthenticateUserUseCase authenticateUserUseCase;

    @Inject
    @Any
    BuildUserJWTUseCase buildUserJWTUseCase;

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
    @WithTransaction
    public Uni<TemplateInstance> register(@Valid @BeanParam RegistrationRequestTO registrationRequestTO) {
        return registerUserUseCase.execute(registrationRequestTO.createRequest())
                .map(u -> authenticationTemplate.data("username", u.id()))
                .log("registration request");
    }

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("authenticate")
    @WithTransaction
    public Uni<Response> authenticate(@Valid @BeanParam AuthenticationRequestTO authenticationRequestTO) {
        return authenticateUserUseCase.execute(authenticationRequestTO.createRequest())
                .flatMap(user -> buildUserJWTUseCase.execute(new BuildUserJWTUseCase.BuildUserJWTUseCaseRequest(user.id())))
                .map(response -> Response.ok()
                        .header("HX-Refresh", true)
                        .cookie(new NewCookie.Builder("access-token")
                                .path("/")
                                .domain("localhost")
                                .maxAge(86400)
                                .value(String.valueOf(response.token()))
                                .comment("jwt access token used for authorization")
                                .httpOnly(true)
                                .build())
                        .build())
                .log("authentication request");
    }

}
