package space.nanobreaker.configuration.monolith.resources;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Location("error/error.qute.html")
    Template error;

    @Override
    public Response toResponse(Throwable exception) {
        return Response.serverError()
                .entity(error.data("message", exception.getMessage()))
                .build();
    }
}
