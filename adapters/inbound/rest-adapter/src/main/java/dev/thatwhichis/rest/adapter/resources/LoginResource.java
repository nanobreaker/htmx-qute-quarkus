package java.dev.thatwhichis.rest.adapter.resources;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.Cache;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {

    @CheckedTemplate(basePath = "login", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
    record login() implements TemplateInstance {}

    @GET
    @WithSpan
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> login() {
        var template = new login();

        return template.createUni();
    }
}