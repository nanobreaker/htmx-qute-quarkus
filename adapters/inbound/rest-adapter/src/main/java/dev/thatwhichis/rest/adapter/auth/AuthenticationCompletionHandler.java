package dev.thatwhichis.rest.adapter.auth;

import dev.thatwhichis.core.ports.inbound.user.UserCommand;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.arc.Unremovable;
import io.quarkus.logging.Log;
import io.quarkus.oidc.AuthenticationCompletionAction;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
@Unremovable
public class AuthenticationCompletionHandler implements AuthenticationCompletionAction {

    private final EventBus eventBus;

    @Inject
    public AuthenticationCompletionHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    @WithSpan("authenticationCompletionHandler")
    public Uni<Void> action(final AuthenticationCompletionContext context) {
        var identity = context.identity();
        var principal = identity.getPrincipal(OidcJwtCallerPrincipal.class);

        var id = UUID.fromString(principal.getClaim(Claims.sub));
        var upn = principal.<String>getClaim(Claims.upn);
        var sid = principal.<String>getClaim("sid");
        var iat = Instant.ofEpochSecond(principal.getClaim(Claims.iat));
        var exp = Instant.ofEpochSecond(principal.getClaim(Claims.exp));

        var command = new UserCommand.Authenticate(id, upn, sid, iat, exp);

        return eventBus
                .<Result<Void, Error>>request("user.authenticated", command)
                .map(Message::body)
                .invoke(result -> {
                    switch (result) {
                        case Ok(_) -> Log.infof("User %s authenticated", upn);
                        case Err(var error) -> Log.error(error.describe());
                    }
                })
                .replaceWithVoid();
    }
}
