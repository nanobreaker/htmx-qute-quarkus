package dev.thatwhichis.rest.adapter.auth;

import io.quarkus.logging.Log;
import io.quarkus.oidc.SecurityEvent;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.jwt.Claims;

import java.util.UUID;

@ApplicationScoped
public class LogoutHandler {

    public void onSecurityEvent(@Observes SecurityEvent event) {
        switch (event.getEventType()) {
            case OIDC_LOGOUT_RP_INITIATED -> {
                var principal = event.getSecurityIdentity().getPrincipal(OidcJwtCallerPrincipal.class);
                var id = UUID.fromString(principal.getClaim(Claims.sub));

                Log.infof("User logged out [id: %s]", id);
            }
            default -> {
                // noop
            }
        }
    }
}
