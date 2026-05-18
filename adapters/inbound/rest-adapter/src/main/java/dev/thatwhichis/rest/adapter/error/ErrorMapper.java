package dev.thatwhichis.rest.adapter.error;

import dev.thatwhichis.rest.adapter.qute.templates.ErrorTemplates;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Objects;

@Provider
public class ErrorMapper implements ExceptionMapper<Throwable> {

    @Override
    @WithSpan("mapErrorToResponse")
    public Response toResponse(final Throwable exception) {
        var exceptionMessage = exception.getMessage();
        var message = Objects.requireNonNullElse(exceptionMessage, exception.getClass().getName());
        var template = ErrorTemplates.error(message);
        var html = template.render();

        return Response.serverError()
                .entity(html)
                .build();
    }
}
