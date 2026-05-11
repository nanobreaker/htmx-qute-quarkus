package dev.thatwhichis.rest.adapter.resources;

import dev.thatwhichis.rest.adapter.services.sse.SseService;
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
public class SseResource {

    private final SseService sseService;
    private final JsonWebToken jwt;

    @Inject
    public SseResource(SseService sseService, JsonWebToken jwt) {
        this.sseService = sseService;
        this.jwt = jwt;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void register(
            @Context final SseEventSink eventSink,
            @Context final Sse sse
    ) {
        var upn = jwt.<String>getClaim(Claims.upn);
        var sid = jwt.<String>getClaim("sid");
        sseService.register(upn, sid, eventSink);
        sseService.publish(upn, sid, sse.newEvent("open"));
    }
}
