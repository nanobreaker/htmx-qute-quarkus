package space.nanobreaker.configuration.monolith.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.jwt.JsonWebToken;
import space.nanobreaker.configuration.monolith.services.sse.SseService;

@Path("sse")
public class SseResource {

    @Inject SseService sseService;
    @Inject JsonWebToken jwt;
    @Context Sse sse;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void register(@Context final SseEventSink eventSink) {
        final String upn = jwt.getClaim("upn");
        final String sid = jwt.getClaim("sid");

        sseService.register(upn, sid, eventSink);
        sseService.publish(upn, sid, sse.newEvent("open"));
    }
}
