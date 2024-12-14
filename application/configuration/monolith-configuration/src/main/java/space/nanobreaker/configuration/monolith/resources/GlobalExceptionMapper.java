package space.nanobreaker.configuration.monolith.resources;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Objects;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Location("error/error.qute.html")
    Template error;

    @Override
    public Response toResponse(final Throwable exception) {
        var exceptionMessage = exception.getMessage();
        var message = Objects.isNull(exceptionMessage)
                ? exception.toString()
                : exceptionMessage;

        return Response.serverError()
                .entity(error.data("message", message))
                .build();
    }
}
