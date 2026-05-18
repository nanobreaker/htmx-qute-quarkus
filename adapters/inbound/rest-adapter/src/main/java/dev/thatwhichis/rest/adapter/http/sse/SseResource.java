package dev.thatwhichis.rest.adapter.http.sse;

import dev.thatwhichis.rest.adapter.sse.SseRegistry;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("sse")
@Authenticated
public class SseResource {

    private final SseRegistry sseRegistry;
    private final JsonWebToken jwt;
    private final Sse sse;

    @Inject
    public SseResource(SseRegistry sseRegistry, JsonWebToken jwt, Sse sse) {
        this.sseRegistry = sseRegistry;
        this.jwt = jwt;
        this.sse = sse;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @WithSpan("register")
    public void register(@Context SseEventSink eventSink) {
        var upn = jwt.<String>getClaim(Claims.upn);
        var sid = jwt.<String>getClaim("sid");
        sseRegistry.register(upn, sid, eventSink);
        sseRegistry.publish(upn, sid, sse.newEvent("open"));
    }
}
