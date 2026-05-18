package dev.thatwhichis.rest.adapter.http.page;

import dev.thatwhichis.rest.adapter.qute.templates.LoginTemplates;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.Cache;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Cache(maxAge = 60 * 60 * 24)
    @WithSpan("login")
    public Uni<String> login() {
        return LoginTemplates.login().createUni();
    }

    @GET
    @Path("auth")
    @WithSpan("auth")
    public Uni<Response> auth() {
        var response = Response.ok()
                .header("HX-Redirect", "/todos")
                .build();

        return Uni.createFrom()
                .item(response);
    }

    @GET
    @Path("register")
    @WithSpan("register")
    public Uni<Response> register() {
        var response = Response.ok()
                .header("HX-Redirect", "/todos")
                .build();

        return Uni.createFrom()
                .item(response);
    }
}