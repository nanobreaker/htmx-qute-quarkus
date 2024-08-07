package space.nanobreaker.configuration.monolith.exception;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Location("exception/error.qute.html")
    Template error;

    @Override
    public Response toResponse(Exception exception) {
        return Response.serverError()
                .entity(error.data("error", exception.getMessage()))
                .build();
    }
}
