package dev.thatwhichis.rest.adapter.resources;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.Cache;

@Path("bottombar")
public class BottombarResource {

    @CheckedTemplate(basePath = "bottombar")
    record bottombar() implements TemplateInstance {

    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    public Uni<String> get() {
        var template = new bottombar();

        return template.createUni();
    }
}
