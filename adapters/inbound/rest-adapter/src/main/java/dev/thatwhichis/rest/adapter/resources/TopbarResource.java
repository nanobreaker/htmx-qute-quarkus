
package java.dev.thatwhichis.rest.adapter.resources;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.jboss.resteasy.reactive.Cache;

@Path("topbar")
public class TopbarResource {

    private final String applicationName;
    private final String applicationVersion;

    @CheckedTemplate(basePath = "topbar")
    record topbar(String applicationName, String applicationVersion, String username) implements TemplateInstance {

    }

    public TopbarResource(
            @ConfigProperty(name = "quarkus.application.name") String applicationName,
            @ConfigProperty(name = "quarkus.application.version") String applicationVersion
    ) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    @GET
    @WithSpan
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> get(@Claim("upn") String upn) {
        var template = new topbar(applicationName, applicationVersion, upn);

        return template.createUni();
    }
}
