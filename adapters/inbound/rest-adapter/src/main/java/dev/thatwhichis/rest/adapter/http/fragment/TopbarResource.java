package dev.thatwhichis.rest.adapter.http.fragment;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.Cache;

@Path("topbar")
@Authenticated
public class TopbarResource {

    private final String applicationName;
    private final String applicationVersion;
    private final JsonWebToken jwt;

    @CheckedTemplate(basePath = "topbar")
    record topbar(String applicationName, String applicationVersion, String username) implements TemplateInstance {

    }

    @Inject
    public TopbarResource(
            @ConfigProperty(name = "quarkus.application.name") String applicationName,
            @ConfigProperty(name = "quarkus.application.version") String applicationVersion, JsonWebToken jwt
    ) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.jwt = jwt;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> get() {
        var username = jwt.<String>getClaim("upn");
        var template = new topbar(applicationName, applicationVersion, username);

        return template.createUni();
    }
}
