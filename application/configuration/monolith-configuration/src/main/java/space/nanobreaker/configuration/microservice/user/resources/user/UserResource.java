package space.nanobreaker.configuration.microservice.user.resources.user;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import space.nanobreaker.configuration.microservice.user.dto.user.AuthenticationRequestTO;
import space.nanobreaker.configuration.microservice.user.dto.user.RegistrationRequestTO;
import space.nanobreaker.core.domain.v1.User;
import space.nanobreaker.core.usecases.v1.user.query.GetUserQuery;

import java.util.UUID;

@Path("user")
@Produces(MediaType.TEXT_HTML)
public class UserResource {

  @Location("v1/user/authentication.qute.html")
  Template authenticationTemplate;

  @Location("v1/user/registration.qute.html")
  Template registrationTemplate;

  @Inject
  EventBus eventBus;

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
  public Uni<TemplateInstance> register(@Valid @BeanParam RegistrationRequestTO registrationRequestTO) {
    return eventBus.<UUID>request("registerUserCommand", registrationRequestTO.toCommand())
        .onItem()
        .transformToUni(response -> eventBus.<User>request("getUserQuery", new GetUserQuery(response.body())))
        .onItem()
        .transform(response -> authenticationTemplate.data("username", response.body().getId()))
        .log("registration request");
  }

  @POST
  @PermitAll
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("authenticate")
  public Uni<Response> authenticate(@Valid @BeanParam AuthenticationRequestTO authenticationRequestTO) {
    return eventBus.<UUID>request("authenticateUserCommand", authenticationRequestTO.toCommand())
        .onItem()
        .transformToUni(response -> eventBus.<User>request("getUserQuery", new GetUserQuery(response.body())))
        .onItem()
        .transform(this::buildAuthenticationResponse)
        .log("authentication request");
  }

  private Response buildAuthenticationResponse(final Message<User> response) {
    final String token = buildJWT(response);
    return Response.ok()
        .header("HX-Refresh", true)
        .cookie(new NewCookie.Builder("access-token")
            .path("/")
            .domain("localhost")
            .maxAge(86400)
            .value(String.valueOf(token))
            .comment("jwt access token used for authorization")
            .httpOnly(true)
            .build())
        .build();
  }

  private String buildJWT(final Message<User> message) {
    return Jwt.issuer("https://example.com/issuer")
        .upn(message.body().getUsername())
        .groups("User")
        .sign();
  }

}
