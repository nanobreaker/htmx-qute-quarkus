package java.dev.thatwhichis.rest.adapter.resources;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.jwt.Claim;
import java.dev.thatwhichis.rest.adapter.services.sse.SseService;

@Path("sse")
public class SseResource {

    private final SseService sseService;

    public SseResource(SseService sseService) {
        this.sseService = sseService;
    }

    @GET
    @WithSpan
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void register(
            @Claim("upn") String upn,
            @Claim("sid") String sid,
            @Context final SseEventSink eventSink,
            @Context final Sse sse
    ) {
        sseService.register(upn, sid, eventSink);
        sseService.publish(upn, sid, sse.newEvent("open"));
    }
}
