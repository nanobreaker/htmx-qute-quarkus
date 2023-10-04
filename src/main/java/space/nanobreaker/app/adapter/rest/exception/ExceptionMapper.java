package space.nanobreaker.app.adapter.rest.exception;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class ExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);

    @Location("exception/error.qute.html")
    Template error;

    @Override
    public Response toResponse(Throwable exception) {
        return Response.serverError()
                .entity(error.data("error", exception.getMessage()))
                .build();
    }
}
