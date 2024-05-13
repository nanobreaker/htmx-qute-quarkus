package space.nanobreaker.configuration.monolith.resources.entry;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class EntryResource {

    @Location("entry/entry.qute.html")
    Template entryTemplate;

    @GET
    public Uni<String> getEntry() {
        return Uni.createFrom().completionStage(entryTemplate.instance().renderAsync());
    }

}