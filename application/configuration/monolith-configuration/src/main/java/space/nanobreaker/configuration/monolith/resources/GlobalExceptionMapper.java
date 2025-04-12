package space.nanobreaker.configuration.monolith.resources;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import space.nanobreaker.configuration.monolith.templates.GlobalTemplates;

import java.util.Objects;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(final Throwable exception) {
        var exceptionMessage = exception.getMessage();
        var message = Objects.isNull(exceptionMessage)
                ? exception.toString()
                : exceptionMessage;
        var template = new GlobalTemplates.error(message);
        var html = template.render();

        return Response.serverError()
                .entity(html)
                .build();
    }
}
